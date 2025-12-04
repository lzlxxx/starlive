package com.starlive.org.controller;

import com.starlive.org.dto.req.VideoTemplateReqDTO;
import com.starlive.org.result.WebResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.starlive.org.dto.resp.VideoTemplateRespDTO;
import com.starlive.org.result.WebResult;
import com.starlive.org.service.VideoRecommendService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 推荐视频控制层
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "视频推荐")
public class VideoRecommendController {
    private final VideoRecommendService videoRecommendService;
    

    @GetMapping("/star-live/video/recommend_video")
    public WebResult<List<VideoTemplateRespDTO>> getVideoFromRecommend(@RequestParam VideoTemplateReqDTO requestParam) {
        return WebResultUtil.success(videoRecommendService.getVideoFromRecommend(requestParam));
    }


}
