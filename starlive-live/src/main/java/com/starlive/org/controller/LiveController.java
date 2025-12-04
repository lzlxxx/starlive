package com.starlive.org.controller;

import com.starlive.org.model.Live;
import com.starlive.org.model.LiveStreamRequest;

import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import com.starlive.org.service.LiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * 直播控制器
 * 提供直播相关的接口
 **/
@RestController
@Api(value = "直播接口", tags = "直播模块接口")
@RequestMapping("/live")
public class LiveController {

    private final LiveService liveService;

    public LiveController(LiveService liveService) {
        this.liveService = liveService;
    }

    /**
     * 创建直播间
     * @param liveRequest 请求参数
     * @return 响应结果
     */
    @PostMapping("/create")
    @Operation(summary = "创建直播间")
    public WebResult<Object> createLive(@Valid @RequestBody Live liveRequest) {
        try {
            liveService.createLive(liveRequest);
            WebResultUtil.success();
            return WebResultUtil.success().setMessage("创建成功").setCode("200");

        } catch (IllegalArgumentException e) {
            return  WebResultUtil.success().setMessage(e.getMessage()).setCode("400");

        } catch (Exception e) {

            return  WebResultUtil.success().setMessage(e.getMessage()).setCode("500");
        }
    }

    /**
     * 获取直播推流信息
     * @param liveStreamRequest 请求参数
     * @return 响应结果
     */
    @PostMapping("/get_live_steam")
    @Operation(summary = "获取直播推流信息")
    public WebResult<Object> getLiveStream(@Valid @RequestBody LiveStreamRequest liveStreamRequest) {
        try {
            Live live = liveService.getLiveStream(liveStreamRequest);
            return WebResultUtil.success().setMessage(live.toJson()).setCode("200");
        } catch (IllegalArgumentException e) {
            return WebResultUtil.failure().setMessage(e.getMessage()).setCode("400");
        } catch (Exception e) {
            return WebResultUtil.failure().setMessage(e.getMessage()).setCode("500");
        }
    }
}

