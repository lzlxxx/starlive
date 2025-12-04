package com.starlive.org.service;

import com.starlive.org.dto.req.VideoTemplateReqDTO;
import com.starlive.org.dto.resp.VideoTemplateRespDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 视频推荐业务接口
 */
@Service
public interface VideoRecommendService {
   List<VideoTemplateRespDTO> getVideoFromRecommend(VideoTemplateReqDTO requestParam);
}
