package com.starlive.org.vo;


import com.starlive.org.pojo.Events;
import com.starlive.org.pojo.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventVideoVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Video> videoList;
    private EventResult event;


}
