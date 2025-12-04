package com.starlive.org.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Data
@NoArgsConstructor
public class EventResult {
    public Long id;//活动id
    public Long organizerId;//活动组织id

    public EventResult(Long event_id,Long organizer_id){
        this.id=event_id;
        this.organizerId=organizer_id;
    }
}
