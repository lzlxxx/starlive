package com.starlive.org.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starlive.org.pojo.Video;

public interface VideoService extends IService<Video> {
    public long getVideoCount(String videoId);
}
