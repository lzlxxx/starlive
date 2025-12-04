package com.starlive.org.controller;//package com.starlive.org.controller;
//
//import com.jayway.jsonpath.internal.path.ArraySliceOperation;
//import com.starlive.org.dto.EventRequest;
//import com.starlive.org.dto.EventScheduleDto;
//import com.starlive.org.mapper.EventScheduleMapper;
//import com.starlive.org.result.WebResult;
//import com.starlive.org.result.WebResultUtil;
//import com.starlive.org.service.EventScheduleService;
//import com.starlive.org.service.EventsService;
//import com.starlive.org.vo.EventDocument;
//import com.starlive.org.vo.EventResult;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/event")
//@Api(value = "活动接口", tags = "活动接口")
//@Validated
//public class EventController {
//    @Autowired
//    private EventsService eventService;
//    @Autowired
//    private EventScheduleService eventScheduleService;
//    @PostMapping("/addEvent")
//    @ApiOperation("创建活动")
//    public WebResult<EventResult> addEvent(@RequestBody EventRequest eventRequest){
//        EventResult eventResult = eventService.createEvent(eventRequest);
//        return WebResultUtil.success(eventResult);
//    }
//    @GetMapping("/{id}")
//    @ApiOperation("获取活动详情")
//    public WebResult<List<EventDocument>> getEvent(@PathVariable String keyword){
//        List<EventDocument> event = eventService.findEvent(keyword);
//        return WebResultUtil.success(event);
//    }
//    @PostMapping("/addSchedule/{id}")
//    @ApiOperation("增加活动日程")
//    public WebResult<Void> addSchedule(@PathVariable("id") Long event_id,@RequestBody List<EventScheduleDto> eventScheduleDto){
//        eventScheduleService.addEventSchedule(event_id,eventScheduleDto);
//        return WebResultUtil.success();
//    }
//}
