package com.starlive.org.service;

import com.starlive.org.model.Live;
import com.starlive.org.model.LiveStreamRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.starlive.org.repository.LiveRepository;
import java.util.Optional;

/**
 * 直播服务类
 **/
@Service
public class LiveService {

    @Autowired
    private LiveRepository liveRepository;

    /**
     * 创建直播
     * @param liveRequest 直播请求对象
     */
    @Transactional
    public void createLive(Live liveRequest) {

        //保存到数据库
        // 检查房间号是否已存在
        if (liveRepository.existsByRoomId(liveRequest.getRoomId())) {
            throw new IllegalArgumentException("房间号已存在");
        }
        // 保存直播数据
        liveRepository.save(liveRequest);
    }

    /**
     * 获取直播推流信息
     * @param liveStreamRequest 直播推流请求对象
     * @return Live 直播信息
     */
    public Live getLiveStream(LiveStreamRequest liveStreamRequest) {
        // 根据直播ID查找直播信息
        Optional<Live> liveOptional = liveRepository.findById(Long.parseLong(liveStreamRequest.getLiveID()));
        return liveOptional.orElseThrow(() -> new IllegalArgumentException("直播未找到"));
    }
}

