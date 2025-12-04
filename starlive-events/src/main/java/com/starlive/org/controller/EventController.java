package com.starlive.org.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starlive.org.dto.EventRequest;
import com.starlive.org.dto.EventScheduleDto;
import com.starlive.org.errcode.BaseErrorCode;
import com.starlive.org.pojo.Events;
import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import com.starlive.org.service.EventScheduleService;
import com.starlive.org.service.EventsService;
import com.starlive.org.vo.EventDocument;
import com.starlive.org.vo.EventResult;
import com.starlive.org.vo.EventVideoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/event")
@Api(value = "活动接口", tags = "活动接口")
public class EventController {
    @Autowired
    private EventsService eventService;
    @Autowired
    private EventScheduleService eventScheduleService;
    @PostMapping("/addEvent")
    @ApiOperation("增加活动")
    public WebResult<EventResult> addEvent(@RequestBody @Validated EventRequest eventRequest){
        EventResult eventResult = eventService.createEvent(eventRequest);
        return WebResultUtil.success(eventResult);
    }
    @GetMapping("/getEvent")
    @ApiOperation("获取活动详情")
    public WebResult<Page<EventDocument>> getEvent(@Validated @RequestParam(value = "keyword",required = false) String keyword,@RequestParam("page") int page,@RequestParam("size") int size){
        Page<EventDocument> event = eventService.findEvent(keyword,page,size);
        return WebResultUtil.success(event);
    }
    @PostMapping("/addSchedule/{id}")
    @ApiOperation("增加活动日程")
    public WebResult<Void> addSchedule(@Validated @PathVariable("id") Long event_id,@RequestBody List<EventScheduleDto> eventScheduleDto) throws IOException {
        eventScheduleService.addEventSchedule(event_id,eventScheduleDto);
        return WebResultUtil.success();
    }

    @GetMapping("/bind")
    @Operation(summary = "视频活动绑定")
    public WebResult<String> bind(
            @RequestParam(value = "eventId",required = true) Long eventId,
            @RequestParam(value = "videoId",required = true) int videoId){

        String code=eventService.bindVideo(eventId,videoId);
        if(code.equals(BaseErrorCode.EVENT_NOT_ERROR.code())){
            return WebResultUtil.failure(BaseErrorCode.EVENT_NOT_ERROR.code(),BaseErrorCode.EVENT_NOT_ERROR.message());
        }else if(code.equals(BaseErrorCode.VIDEO_NOT_ERROR.code())){
            return WebResultUtil.failure(BaseErrorCode.VIDEO_NOT_ERROR.code(),BaseErrorCode.VIDEO_NOT_ERROR.message());
        }else return WebResultUtil.success();
    }
    @GetMapping("/recommend")
    @Operation(summary = "推荐信息")
    public WebResult<Page<EventDocument>> recommend(
            @RequestParam(value = "latitude",required = true) double latitude, @RequestParam(value = "longitude",required = true) double longitude,
            @RequestParam(value = "radius",required = true) int radius,@RequestParam(value = "page") int page,@RequestParam(value = "size",required = true)int size) throws JsonProcessingException {
        Page<EventDocument> list = eventService.findEventsNearby(latitude,longitude,radius,page,size);
        if(!list.isEmpty()) {
            return WebResultUtil.success(list);
        }else {
            return WebResultUtil.failure("402","附近无活动");
        }


    }
    @GetMapping("/recommend/video")
    @Operation(summary = "推荐活动视频")
    public WebResult<Page<EventVideoVo>> recommendVideo(
            @RequestParam(value = "latitude",required = true) double latitude,@RequestParam(value = "longitude",required = true) double longitude,
            @RequestParam(value = "radius",required = true) int radius,@RequestParam(value = "page",required = true) int page,@RequestParam(value = "size",required = true)int size) throws JsonProcessingException {
        Page<EventVideoVo> eventVideoVos=eventService.getVideos(latitude,longitude,radius,page,size);
        if(!eventVideoVos.isEmpty()) {
            return WebResultUtil.success(eventVideoVos);
        }else return WebResultUtil.failure();
    }
}
