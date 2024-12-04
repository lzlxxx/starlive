package com.starlive.org.controller;

import com.starlive.org.dto.EventRequest;
import com.starlive.org.dto.EventScheduleDto;
import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import com.starlive.org.service.EventScheduleService;
import com.starlive.org.service.EventsService;
import com.starlive.org.vo.EventDocument;
import com.starlive.org.vo.EventResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
@Api(value = "活动接口", tags = "活动接口")
@Validated
public class EventController {
    @Autowired
    private EventsService eventService;
    @Autowired
    private EventScheduleService eventScheduleService;
    @PostMapping("/addEvent")
    @ApiOperation("增加活动")
    public WebResult<EventResult> addEvent(@RequestBody EventRequest eventRequest){
        EventResult eventResult = eventService.createEvent(eventRequest);
        return WebResultUtil.success(eventResult);
    }
    @GetMapping("/getEvent")
    @ApiOperation("获取活动详情")
    public WebResult<Page<EventDocument>> getEvent(@RequestParam("keyword") String keyword,@RequestParam("page") int page,@RequestParam("size") int size){
        Page<EventDocument> event = eventService.findEvent(keyword,page,size);
        return WebResultUtil.success(event);
    }
    @PostMapping("/addSchedule/{id}")
    @ApiOperation("增加活动日程")
    public WebResult<Void> addSchedule(@PathVariable("id") Long event_id,@RequestBody List<EventScheduleDto> eventScheduleDto){
        eventScheduleService.addEventSchedule(event_id,eventScheduleDto);
        return WebResultUtil.success();
    }
}
