package com.starlive.org;

import com.starlive.org.dto.EventRequest;
import com.starlive.org.dto.EventRewardDto;
import com.starlive.org.dto.EventScheduleDto;
import com.starlive.org.elasticrepository.EventDocumentRepository;
import com.starlive.org.mapper.*;
import com.starlive.org.pojo.*;
import com.starlive.org.service.EventScheduleService;
import com.starlive.org.service.impl.EventsServiceImpl;
import com.starlive.org.vo.EventDocument;
import com.starlive.org.vo.EventRewardsVo;
import com.starlive.org.vo.EventScheduleVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class StarliveEventsApplicationTests {
    @Autowired
    private EventsMapper eventsMapper;
    @Autowired
    private EventOrganizersMapper eventOrganizersMapper;
    @Autowired
    private EventStatusLogsMapper statusLogsMapper;
    @Autowired
    private EventRewardsMapper eventRewardMapper;

    @Autowired
    private EventDocumentRepository eventDocumentRepository;
    @Autowired
    private EventScheduleService eventScheduleService;


    @Test
    void contextLoads() {
    }

    @Test
    void hello(){
                  List<EventScheduleDto> eventScheduleDtoList=new ArrayList<>();
//        eventScheduleDtoList.add(new EventScheduleDto("Opening Ceremony",new Date(),"Main Hall, Conference Center","The grand opening of the Tech Innovation Summit."));
//        eventScheduleDtoList.add(new EventScheduleDto("Keynote Speech",new Date(),"Main Hall, Conference Center","A keynote speech by industry experts."));
        eventScheduleDtoList.add(new EventScheduleDto("uuuuuuu",new Date(),"Workshop Rooms, Conference Center","A series of workshops on various topics."));
        System.out.println(eventScheduleDtoList);
        eventScheduleService.addEventSchedule(50L,eventScheduleDtoList);

    }

}
