package com.starlive.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlive.org.dto.EventRequest;
import com.starlive.org.dto.EventRewardDto;
import com.starlive.org.elasticrepository.EventDocumentRepository;
import com.starlive.org.errcode.BaseErrorCode;
import com.starlive.org.exception.ServiceException;
import com.starlive.org.mapper.*;
import com.starlive.org.pojo.*;
import com.starlive.org.service.EventsService;
import com.starlive.org.util.RedisUtil;
import com.starlive.org.vo.EventDocument;
import com.starlive.org.vo.EventResult;
import com.starlive.org.vo.EventRewardsVo;
import com.starlive.org.vo.EventVideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author nan
* @description 针对表【events】的数据库操作Service实现
* @createDate 2024-11-09 00:36:04
*/
@Slf4j
@Service
public class EventsServiceImpl extends ServiceImpl<EventsMapper, Events> implements EventsService {


    @Autowired
    private EventsMapper eventMapper;

    @Autowired
    private EventOrganizersMapper eventOrganizerMapper;

    @Autowired
    private EventStatusLogsMapper eventStatusLogMapper;

    @Autowired
    private EventRewardsMapper eventRewardMapper;

    @Autowired
    private EventDocumentRepository eventDocumentRepository;

    private static final int Time=60;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private RedisUtil redisUtil;


    // 创建活动
    @Transactional
    public EventResult createEvent(EventRequest eventDto) {
        try {
            // 1. 创建活动
            Events event = createEventEntity(eventDto);
            eventMapper.insertEvent(event);

            // 2. 添加组织者信息
            EventOrganizers organizer = createOrganizer(eventDto, event.getId());
            eventOrganizerMapper.insertOrganizer(organizer);
            log.debug("创建活动组织者:{}",organizer);

            // 3. 记录活动状态
            logEventStatus(eventDto, event);

            // 4. 处理奖励信息
            List<EventRewards> eventRewards = handleEventRewards(eventDto, event);

            // 5. 保存到es
            saveEventToElasticSearch(event, eventDto,eventRewards);
            return new EventResult(event.getId(), organizer.getId());
        } catch (Exception e) {
            log.error("创建活动失败", e);
            throw new ServiceException("创建活动失败");
        }
    }

    // 创建活动
    private Events createEventEntity(EventRequest eventDto) {
        Events event = new Events();
        BeanUtils.copyProperties(eventDto, event);
        event.setStatus("draft");
        return event;
    }

    // 创建活动组织者
    private EventOrganizers createOrganizer(EventRequest eventDto, Long eventId) {
        EventOrganizers organizer = new EventOrganizers();
        organizer.setEventId(eventId);
        organizer.setOrganizerId(eventDto.getOrganizerId());
        organizer.setRole("host");
        return organizer;
    }

    // 记录活动状态
    private void logEventStatus(EventRequest eventDto, Events event) {
        EventStatusLogs statusLog = new EventStatusLogs();
        statusLog.setEventId(event.getId());
        statusLog.setOldStatus("draft");
        statusLog.setNewStatus("draft");
        statusLog.setChangedBy(eventDto.getOrganizerId());
        eventStatusLogMapper.insertStatusLog(statusLog);
        log.debug("记录活动状态:{}",statusLog);
    }

    // 处理活动奖励
    private  List<EventRewards> handleEventRewards(EventRequest eventDto, Events event) {
        List<EventRewardDto> eventRewards = eventDto.getRewards();
        if (eventRewards != null && !eventRewards.isEmpty()) {
            //4.1将dto的奖励字段数据赋值给插入数据库的奖励类
            List<EventRewards> rewards = eventRewards.stream()
                    .map(rewardDto -> createEventReward(rewardDto, event))
                    .collect(Collectors.toList());
            log.debug("插入奖励: {}", rewards);
            rewards.forEach(reward -> eventRewardMapper.insertReward(reward));
            return rewards;
        }
        return null;
    }

    // 创建单个活动奖励
    private EventRewards createEventReward(EventRewardDto rewardDto, Events event) {
        EventRewards reward = new EventRewards();
        reward.setEventId(event.getId());
        reward.setRewardName(rewardDto.getRewardName());
        reward.setDescription(rewardDto.getRewardDescription());
        reward.setQuantity(rewardDto.getRewardQuantity());
        return reward;
    }

    // 保存活动到 Elasticsearch
    private void saveEventToElasticSearch(Events event, EventRequest eventDto,List<EventRewards> eventRewards) {
        //5.1 创建索引库实例
        EventDocument eventDocument = new EventDocument();
        //5.2 将实体类对象转换为索引库对象
        BeanUtils.copyProperties(event, eventDocument);
        //5.3 赋值组织者的姓名
        eventDocument.setOrganizerName(eventDto.getOrganizerName());
        // 5.4确保 rewards 字段始终是非 null 的列表
        List<EventRewardsVo> eventRewardsVoList = getEventRewardsVos(eventRewards);
        //5.5 如果没有奖励  直接设置为空列表，避免传递 null
        eventDocument.setRewards(eventRewardsVoList);
        //5.6 确保 schedule 字段始终是非 null 的列表  因为活动表得要后续用户添加
        eventDocument.setSchedule(new ArrayList<>());
        //5.7 设置删除标志
        eventDocument.setDelFlag(0);
        try {
            //5.8 保存到 Elasticsearch
            eventDocumentRepository.save(eventDocument);
            log.info("保存活动到 Elasticsearch 成功:{}", eventDocument);
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (!msg.contains("201 Created") && !msg.contains("200 OK")) {
                log.error("保存活动到 Elasticsearch 失败", e);
                throw new ServiceException("保存活动到 Elasticsearch 失败: " + msg);
            }
        }
    }

    private static List<EventRewardsVo> getEventRewardsVos(List<EventRewards> eventRewards) {
        List<EventRewardsVo> eventRewardsVoList = new ArrayList<>();
        if (eventRewards != null && !eventRewards.isEmpty()) {
            for (EventRewards eventReward : eventRewards) {
                EventRewardsVo eventRewardsVo = new EventRewardsVo();
                //5.4.1 只保留用户可以看到的奖励的字段  vo
                eventRewardsVo.setRewardId(eventReward.getId());
                eventRewardsVo.setRewardName(eventReward.getRewardName());
                eventRewardsVo.setRewardDescription(eventReward.getDescription());
                eventRewardsVo.setRewardQuantity(eventReward.getQuantity());
                //5.4.2 添加奖励
                eventRewardsVoList.add(eventRewardsVo);
            }
        }
        return eventRewardsVoList;
    }
    // 根据关键字获取活动
    @Override
    public Page<EventDocument> findEvent(String keyword, int page, int size) {
        try {
            //1. 设置分页属性 以id升序排序
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
            //2. 如果关键字为空，则分页返回所有活动
            if(keyword==null||keyword.isEmpty()){
                return eventDocumentRepository.findAllWithPagination(pageable);
            }
            //3. 如果关键字不为空，则根据关键字分页返回活动
            return eventDocumentRepository.findEvent(keyword,pageable);
        } catch (Exception e) {
            log.error("根据关键字获取活动失败", e);
            return null;
        }
    }
    @Override
    public Page<EventDocument> findEventsNearby(double latitude, double longitude, int radius, int page, int size) throws JsonProcessingException {
        //es分页检索附件活动
        String cacheKey="EventsNearby:lat:"+latitude+",lon:"+longitude+",radius:"+radius;
        ObjectMapper objectMapper = new ObjectMapper();
        List<EventDocument> eventsList = null;
        //1.从缓存获取
        String ListEventsJson= (String) redisUtil.get(cacheKey);
        if(ListEventsJson!=null){
            log.info("--------"+ListEventsJson);
            eventsList=objectMapper.readValue(ListEventsJson,new TypeReference<List<EventDocument>>(){});
            log.info("8888");
            if(eventsList.isEmpty()){
                Page<EventDocument> page1=new PageImpl<>(eventsList,PageRequest.of(page, size), eventsList.size());
                log.info("88888"+page1.toString());
                return page1;
            }
        }else {
            String lockKey="lock:EventsNearby:lat:"+latitude+",lon:"+longitude+",radius:"+radius;
            String lockValue= UUID.randomUUID().toString();
            try{
                Boolean isLockAcquired=redisUtil.setIfAbsent(lockKey,lockValue,10, TimeUnit.SECONDS);
                if(Boolean.TRUE.equals(isLockAcquired)){
                    ListEventsJson= (String) redisUtil.get(cacheKey);
                    if(ListEventsJson!=null){
                        eventsList=objectMapper.readValue(ListEventsJson, new TypeReference<List<EventDocument>>() {});
                        if(!eventsList.isEmpty()){
                            return new PageImpl<>(eventsList, PageRequest.of(page, size), eventsList.size());
                        }
                    }else {
                        //从es获取
                        Pageable pageRequest = PageRequest.of(page, size);
                        eventsList=eventDocumentRepository.findEventsByGeoLocation(latitude,longitude,radius,pageRequest).getContent();
                        if(!eventsList.isEmpty()){
                            ListEventsJson= objectMapper.writeValueAsString(eventsList);
                            log.info("++++"+ListEventsJson);
                            redisUtil.set(cacheKey,ListEventsJson,Time,TimeUnit.SECONDS);

                        }
                    }
                }else {
                    for (int i=0;i<5;i++) {
                        Thread.sleep(1000);
                        ListEventsJson = (String) redisUtil.get(cacheKey);
                        if (ListEventsJson != null) {
                            eventsList=objectMapper.readValue(ListEventsJson, new TypeReference<List<EventDocument>>() {});
                            return new PageImpl<>(eventsList, PageRequest.of(page, size), eventsList.size());
                        }
                    }
                }
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                // Lua 脚本
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) " +
                        "else return 0 end";
                // 执行 Lua 脚本
                redisUtil.executeLuaScript(luaScript,lockKey,lockValue);
            }

        }
        return new PageImpl<>(eventsList, PageRequest.of(page, size), eventsList.size());
    }



    @Override
    public Page<EventVideoVo> getVideos(double latitude, double longitude, int radius,int page,int size) throws JsonProcessingException {

//        for (Events event : eventsList) {
//            List<Video> videos=videoMapper.selectVideoList(event.getId());
//            EventVideoVo eventVideoVo=new EventVideoVo();
//            eventVideoVo.setVideoList(videos);
//            eventVideoVo.setEvnet(event);
//
//        }
        List<EventVideoVo> videoVoList = null;
        String cacheKey = "getVedios:lat:" + latitude + ",lon:" + longitude + ",radius:" + radius;
        ObjectMapper objectMapper = new ObjectMapper();
        String ListEventVedioJson = (String) redisUtil.get(cacheKey);
        //1.从缓存获取
        if (ListEventVedioJson != null) {
            videoVoList = objectMapper.readValue(ListEventVedioJson, new TypeReference<List<EventVideoVo>>() {
            });
            if (!videoVoList.isEmpty()) {
                // 将 List 转换为 Page
                return new PageImpl<>(videoVoList, PageRequest.of(page, size), videoVoList.size());
            }
        } else {
            String lockKey = "lock:getVedios:lat:" + latitude + ",lon:" + longitude + ",radius:" + radius;
            String lockValue = UUID.randomUUID().toString();
            try {
                Boolean isLockAcquired = redisUtil.setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(isLockAcquired)) {
                    ListEventVedioJson = (String) redisUtil.get(cacheKey);
                    if (ListEventVedioJson != null) {
                        videoVoList = objectMapper.readValue(ListEventVedioJson, new TypeReference<List<EventVideoVo>>() {
                        });
                        if (!videoVoList.isEmpty()) {
                            // 将 List 转换为 Page
                            return new PageImpl<>(videoVoList, PageRequest.of(page, size), videoVoList.size());
                        }
                    } else {
                        //从es获取
                        //1.通过es分页查询附件活动
                        List<EventDocument> eventsList = this.findEventsNearby(latitude, longitude, radius, page, size).getContent();
                        log.info("==========="+eventsList);

                        //2.Stream流将video和event重新封装成集合EventVideoVo
                        videoVoList = eventsList.stream().map(eventDocument -> {
                            System.out.println(eventDocument.getId());
                            List<Video> videos = videoMapper.selectVideoList(eventDocument.getId());
                            EventResult eventResult=new EventResult();
                            BeanUtils.copyProperties(eventDocument,eventResult);
                            return new EventVideoVo(videos,eventResult);
                        }).collect(Collectors.toList());
                        if (!videoVoList.isEmpty()) {
                            ListEventVedioJson = objectMapper.writeValueAsString(videoVoList);
                            redisUtil.set(cacheKey, ListEventVedioJson, Time, TimeUnit.SECONDS);
                            log.info(ListEventVedioJson);
                            // 将 List 转换为 Page
                            return new PageImpl<>(videoVoList, PageRequest.of(page, size), videoVoList.size());
                        }
                    }
                } else {
                    for (int i = 0; i < 5; i++) {
                        Thread.sleep(1000);
                        ListEventVedioJson = (String) redisUtil.get(cacheKey);
                        if (ListEventVedioJson != null) {
                            videoVoList = objectMapper.readValue(ListEventVedioJson, new TypeReference<List<EventVideoVo>>() {
                            });
                            // 将 List 转换为 Page
                            return new PageImpl<>(videoVoList, PageRequest.of(page, size), videoVoList.size());
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } finally {
                // Lua 脚本
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) " +
                        "else return 0 end";
                // 执行 Lua 脚本
                redisUtil.executeLuaScript(luaScript, lockKey, lockValue);
            }
        }
        // 如果 videoVoList 为空，返回一个空的 Page
        return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), 0);
    }

    @Transactional
    @Override
    public String bindVideo(Long eventId, int videoId) {
        Events event=eventMapper.selectEvent(eventId);
        if (event==null){
            return BaseErrorCode.EVENT_NOT_ERROR.code() ;
        }
        Video video=videoMapper.selectVideo(videoId);
        if(video==null){
            return BaseErrorCode.VIDEO_NOT_ERROR.code() ;
        }
        video.setEventId(eventId);
        //将活动ID绑定到视频表
        videoMapper.updateEventId(eventId,video.getVideoId());
        return "200";
    }
}

