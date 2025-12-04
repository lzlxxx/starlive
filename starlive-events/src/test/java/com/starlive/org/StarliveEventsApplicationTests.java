package com.starlive.org;//package com.starlive.org;

import co.elastic.clients.elasticsearch._types.GeoLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlive.org.dto.EventRequest;
import com.starlive.org.dto.EventRewardDto;
import com.starlive.org.dto.EventScheduleDto;
import com.starlive.org.elasticrepository.EventDocumentRepository;
import com.starlive.org.errcode.BaseErrorCode;
import com.starlive.org.exception.ServiceException;
import com.starlive.org.mapper.*;
import com.starlive.org.pojo.*;
import com.starlive.org.service.EventScheduleService;
import com.starlive.org.service.impl.EventsServiceImpl;
import com.starlive.org.util.RedisUtil;
import com.starlive.org.vo.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest
class StarliveEventsApplicationTests {
//    @Autowired
//    private EventsMapper eventsMapper;
//    @Autowired
//    private EventOrganizersMapper eventOrganizersMapper;
//    @Autowired
//    private EventStatusLogsMapper statusLogsMapper;
//    @Autowired
//    private EventRewardsMapper eventRewardMapper;
//
//    @Autowired
//    private EventDocumentRepository eventDocumentRepository;
//    @Autowired
//    private EventScheduleService eventScheduleService;
//
//
//    @Test
//    void contextLoads() {
//    }
//
//    @Test
//    void hello(){
//                  List<EventScheduleDto> eventScheduleDtoList=new ArrayList<>();
//        eventScheduleDtoList.add(new EventScheduleDto("Opening Ceremony",new Date(),"Main Hall, Conference Center","The grand opening of the Tech Innovation Summit."));
//        eventScheduleDtoList.add(new EventScheduleDto("Keynote Speech",new Date(),"Main Hall, Conference Center","A keynote speech by industry experts."));
//        eventScheduleDtoList.add(new EventScheduleDto("Workshops",new Date(),"Workshop Rooms, Conference Center","A series of workshops on various topics."));
//        System.out.println(eventScheduleDtoList);
//        eventScheduleService.addEventSchedule(35L,eventScheduleDtoList);
//
//    }
    @Autowired
    private VideoMapper videoMapper;
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

            // 3. 记录活动状态
            logEventStatus(eventDto, event);

            // 4. 处理奖励信息
            List<EventRewards> eventRewards = handleEventRewards(eventDto, event);

            // 5. 保存到es
            saveEventToElasticSearch(event, eventDto,eventRewards);
            return new EventResult(event.getId(), organizer.getId());
        } catch (Exception e) {
            e.printStackTrace();
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
    }

    // 处理活动奖励
    private  List<EventRewards> handleEventRewards(EventRequest eventDto, Events event) {
        List<EventRewardDto> eventRewards = eventDto.getRewards();
        if (eventRewards != null && !eventRewards.isEmpty()) {
            //4.1将dto的奖励字段数据赋值给插入数据库的奖励类
            List<EventRewards> rewards = eventRewards.stream()
                    .map(rewardDto -> createEventReward(rewardDto, event))
                    .collect(Collectors.toList());
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
        List<EventRewardsVo> eventRewardsVoList = new ArrayList<>();
        if (eventRewards != null && !eventRewards.isEmpty()) {
            for (EventRewards eventReward : eventRewards) {
                EventRewardsVo eventRewardsVo = new EventRewardsVo();
                //5.4.1 只保留用户可以看到的奖励的字段  vo
                BeanUtils.copyProperties(eventReward, eventRewardsVo);
                //5.4.2 添加奖励
                eventRewardsVoList.add(eventRewardsVo);
            }
        }
        //5.5 如果没有奖励  直接设置为空列表，避免传递 null
        eventDocument.setRewards(eventRewardsVoList);
        //5.6 确保 schedule 字段始终是非 null 的列表  因为活动表得要后续用户添加
        eventDocument.setSchedule(new ArrayList<>());
        //5.7 设置删除标志
        eventDocument.setDelFlag(0);
        try {
            //5.8 保存到 Elasticsearch
            eventDocumentRepository.save(eventDocument);
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (!msg.contains("201 Created") && !msg.contains("200 OK")) {
                throw new ServiceException("保存活动到 Elasticsearch 失败: " + msg);
            }
        }
    }



    public Page<EventDocument> findEventsNearby(double latitude, double longitude, int radius, int page, int size) throws JsonProcessingException {
        //es分页检索附件活动
        String cacheKey="EventsNearby:lat:"+latitude+",long:"+longitude+",radius:"+radius;
        ObjectMapper objectMapper = new ObjectMapper();
        List<EventDocument> eventsList = null;
        //1.从缓存获取
        String ListEventsJson= (String) redisUtil.get(cacheKey);
        if(ListEventsJson!=null){
            eventsList=objectMapper.readValue(ListEventsJson,new TypeReference<List<EventDocument>>(){});
            if(!eventsList.isEmpty()){
                return new PageImpl<>(eventsList, PageRequest.of(page, size), eventsList.size());
            }
        }else {
            String lockKey="lat:"+latitude+",long:"+longitude+",radius:"+radius;
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

//    public Page<EventVideoVo> getVedios(double latitude, double longitude, int radius, int page, int size) throws JsonProcessingException {
//
////        for (Events event : eventsList) {
////            List<Video> videos=videoMapper.selectVideoList(event.getId());
////            EventVideoVo eventVideoVo=new EventVideoVo();
////            eventVideoVo.setVideoList(videos);
////            eventVideoVo.setEvnet(event);
////
////        }
//        List<EventVideoVo> videoVoList = null;
//        String cacheKey = "getVedios:lat:" + latitude + ",long:" + longitude + ",radius:" + radius;
//        ObjectMapper objectMapper = new ObjectMapper();
//        String ListEventVedioJson = (String) redisUtil.get(cacheKey);
//        //1.从缓存获取
//        if (ListEventVedioJson != null) {
//            videoVoList = objectMapper.readValue(ListEventVedioJson, new TypeReference<List<EventVideoVo>>() {
//            });
//            if (!videoVoList.isEmpty()) {
//                // 将 List 转换为 Page
//                return new PageImpl<>(videoVoList, PageRequest.of(page, size), videoVoList.size());
//            }
//        } else {
//            String lockKey = "getVedios:lat:" + latitude + ",long:" + longitude + ",radius:" + radius;
//            String lockValue = UUID.randomUUID().toString();
//            try {
//                Boolean isLockAcquired = redisUtil.setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);
//                if (Boolean.TRUE.equals(isLockAcquired)) {
//                    ListEventVedioJson = (String) redisUtil.get(cacheKey);
//                    if (ListEventVedioJson != null) {
//                        videoVoList = objectMapper.readValue(ListEventVedioJson, new TypeReference<List<EventVideoVo>>() {
//                        });
//                        if (!videoVoList.isEmpty()) {
//                            // 将 List 转换为 Page
//                            return new PageImpl<>(videoVoList, PageRequest.of(page, size), videoVoList.size());
//                        }
//                    } else {
//                        //从es获取
//                        //1.通过es分页查询附件活动
//                        List<EventDocument> eventsList = this.findEventsNearby(latitude, longitude, radius, page, size).getContent();
//                        //2.Stream流将video和event重新封装成集合EventVideoVo
//                        videoVoList = eventsList.stream().map(events -> {
//                            List<Video> videos = videoMapper.selectVideoList(events.getId());
//                            return new EventVideoVo(videos,events);
//                        }).collect(Collectors.toList());
//                        if (!videoVoList.isEmpty()) {
//                            ListEventVedioJson = objectMapper.writeValueAsString(videoVoList);
//                            redisUtil.set(cacheKey, ListEventVedioJson, Time, TimeUnit.SECONDS);
//                            // 将 List 转换为 Page
//                            return new PageImpl<>(videoVoList, PageRequest.of(page, size), videoVoList.size());
//                        }
//                    }
//                } else {
//                    for (int i = 0; i < 5; i++) {
//                        Thread.sleep(1000);
//                        ListEventVedioJson = (String) redisUtil.get(cacheKey);
//                        if (ListEventVedioJson != null) {
//                            videoVoList = objectMapper.readValue(ListEventVedioJson, new TypeReference<List<EventVideoVo>>() {
//                            });
//                            // 将 List 转换为 Page
//                            return new PageImpl<>(videoVoList, PageRequest.of(page, size), videoVoList.size());
//                        }
//                    }
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            } finally {
//                // Lua 脚本
//                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
//                        "return redis.call('del', KEYS[1]) " +
//                        "else return 0 end";
//                // 执行 Lua 脚本
//                redisUtil.executeLuaScript(luaScript, lockKey, lockValue);
//            }
//        }
//        // 如果 videoVoList 为空，返回一个空的 Page
//        return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), 0);
//    }

    @Transactional

    public String bindVedio(Long eventId, int vedioId) {
        Events event=eventMapper.selectEvent(eventId);
        if (event==null){
            return BaseErrorCode.EVENT_NOT_ERROR.code() ;
        }
        Video video=videoMapper.selectVideo(vedioId);
        if(video==null){
            return BaseErrorCode.VIDEO_NOT_ERROR.code() ;
        }
        video.setEventId(eventId);
        //将活动ID绑定到视频表
        videoMapper.updateEventId(eventId,video.getVideoId());
        return "200";
    }
    @Autowired
    private EventsServiceImpl eventsService;
    @Test
    public void test1() throws JsonProcessingException {
        //System.out.println(videoMapper.selectVideo(1));
        EventRequest events = new EventRequest();
        events.setOrganizerName("xxxxxxxx");
        events.setDescription("test");
        events.setTitle("test");
        events.setLocation("xxx");
        events.setStartTime(new Date());
        events.setEndTime(new Date());

        events.setLocationPoint(34.0522,-118.2437);
        events.setOrganizerId(2L);
        events.setRewards(new ArrayList<>());
        System.out.println(events);
        createEvent(events);
        ;
        System.out.println(videoMapper.selectVideoList(48L));
        System.out.println(eventsService.getVideos(34.0522,-118.2437,10,0,10));
    }
    @Test
    public void test2(){
        Events events=new Events();
        events.setTitle("test");
        events.setDescription("test");
        events.setStartTime(new Date());
        events.setEndTime(new Date());
        events.setLocation("test");
        events.setLocationPoint(34.0522,-118.2437);
        events.setOrganizerId(2L);
        events.setStatus("draft");
        events.setPosterUrl("test");
        events.setCreateTime(new Date());
        events.setUpdateTime(new Date());
        events.setDelFlag(0);
        events.setId(48L);
        eventMapper.insertEvent(events);
    }
//
}
