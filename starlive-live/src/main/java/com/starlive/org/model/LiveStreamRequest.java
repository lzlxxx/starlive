package com.starlive.org.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.starlive.org.result.WebResult;


/**
 * 获取直播推流请求模型
 **/
@Data
public class LiveStreamRequest extends WebResult{
    @NotNull(message = "userId 不能为空")
    private String userId; // 用户ID
    @NotNull(message = "liveID 不能为空")
    private String liveID; // 直播ID
    private Integer isLiveStream; // 是否直播账号
}

