package com.starlive.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starlive.org.dto.EventRequest;
import com.starlive.org.dto.EventRewardDto;
import com.starlive.org.elasticrepository.EventDocumentRepository;
import com.starlive.org.exception.ServiceException;
import com.starlive.org.mapper.*;
import com.starlive.org.pojo.*;
import com.starlive.org.service.EventsService;
import com.starlive.org.vo.EventDocument;
import com.starlive.org.vo.EventResult;
import com.starlive.org.vo.EventRewardsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author nan
* @description 针对表【events】的数据库操作Service实现
* @createDate 2024-11-09 00:36:04
*/
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
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (!msg.contains("201 Created") && !msg.contains("200 OK")) {
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
                return eventDocumentRepository.findAll(pageable);
            }
            //3. 如果关键字不为空，则根据关键字分页返回活动
            return eventDocumentRepository.findEvent(keyword,pageable);
        } catch (Exception e) {
            return null;
        }
    }
}

