package com.starlive.org.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starlive.org.dto.EventRequest;
import com.starlive.org.pojo.EventRewards;
import com.starlive.org.pojo.Events;
import com.baomidou.mybatisplus.extension.service.IService;
import com.starlive.org.vo.EventDocument;
import com.starlive.org.vo.EventResult;
import com.starlive.org.vo.EventVideoVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
* @author nan
* @description 针对表【events】的数据库操作Service
* @createDate 2024-11-09 00:36:04
*/
public interface EventsService extends IService<Events> {
    EventResult createEvent(EventRequest events);
    Page<EventDocument> findEvent(String keyword, int page, int size);


    Page<EventDocument> findEventsNearby(double latitude, double longitude, int radius, int page, int size) throws JsonProcessingException;



    Page<EventVideoVo> getVideos(double latitude, double longitude, int radius, int page, int size) throws JsonProcessingException;

    String bindVideo(Long eventId, int videoId);
}


