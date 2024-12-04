package com.starlive.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starlive.org.dto.EventScheduleDto;
import com.starlive.org.elasticrepository.EventDocumentRepository;
import com.starlive.org.exception.ServiceException;
import com.starlive.org.pojo.EventSchedule;
import com.starlive.org.service.EventScheduleService;
import com.starlive.org.mapper.EventScheduleMapper;
import com.starlive.org.vo.EventDocument;
import com.starlive.org.vo.EventScheduleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author nan
* @description 针对表【event_schedule】的数据库操作Service实现
* @createDate 2024-11-20 16:54:21
*/
@Service
public class EventScheduleServiceImpl extends ServiceImpl<EventScheduleMapper, EventSchedule>
    implements EventScheduleService{
    @Autowired
    private EventScheduleMapper eventScheduleMapper;
    @Autowired
    private EventDocumentRepository eventDocumentRepository;
    @Override
    public void addEventSchedule(Long event_id, List<EventScheduleDto> eventScheduleDtoList) {
        // 1.从 es 获取现有的 EventDocument 如果没有活动则报错
        EventDocument eventDocument = eventDocumentRepository.findById(event_id)
                .orElseThrow(() -> new ServiceException("无该活动"));
        List<EventSchedule> schedules = eventScheduleMapper.findByEventId(event_id);
        // 2.循环处理每个 EventScheduleDto
        List<EventScheduleVo> eventScheduleVoList = new ArrayList<>();
        // 3.处理数据
        for (EventScheduleDto scheduleDto : eventScheduleDtoList) {
            EventSchedule eventSchedule = new EventSchedule();
            // 3.1将 EventScheduleDto 复制到 EventSchedule
            eventSchedule.setScheduleName(scheduleDto.getScheduleName());
            eventSchedule.setScheduleTime(scheduleDto.getScheduleTime());
            eventSchedule.setLocation(scheduleDto.getScheduleLocation());
            eventSchedule.setDescription(scheduleDto.getScheduleDescription());
            eventSchedule.setEventId(event_id);
            // 3.2插入数据库
            eventScheduleMapper.insertSchedule(eventSchedule);
            // 3.3将 EventSchedule 转换为 EventScheduleVo
            EventScheduleVo eventScheduleVo = new EventScheduleVo();
            eventScheduleVo.setScheduleId(eventSchedule.getId());
            eventScheduleVo.setScheduleName(eventSchedule.getScheduleName());
            eventScheduleVo.setScheduleTime(eventSchedule.getScheduleTime());
            eventScheduleVo.setScheduleLocation(eventSchedule.getLocation());
            eventScheduleVo.setScheduleDescription(eventSchedule.getDescription());
            eventScheduleVoList.add(eventScheduleVo);
        }
        if (eventDocument != null) {
            //3.4 更新 schedule 字段
            List<EventScheduleVo> mergedSchedule = Stream.concat(eventDocument.getSchedule().stream(), eventScheduleVoList.stream())
                    .collect(Collectors.toMap(
                            EventScheduleVo::getScheduleName,   // 根据 scheduleName 作为键
                            eventScheduleVo -> eventScheduleVo,
                            (existing, replacement) -> replacement)) // 如果 scheduleName 相同，保留最新的值
                    .values()
                    .stream()
                    .sorted(Comparator.comparing(EventScheduleVo::getScheduleTime)) // 按照 scheduleTime 排序
                    .collect(Collectors.toList());
            eventDocument.setSchedule(mergedSchedule);
            // 3.5保存更新后的文档到 Elasticsearch
            try {
                eventDocumentRepository.save(eventDocument);
            } catch (Exception e) {
                String msg = e.getMessage();
                if(!msg.contains("201 Created") && !msg.contains("200 OK")) {
                    throw e;
                }
            }
        }
    }
}




