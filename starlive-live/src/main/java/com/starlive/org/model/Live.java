package com.starlive.org.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 直播请求模型
 **/
@Entity
@Data
public class Live {
    @NotNull(message = "userId 不能为空")
    private Long userId; // 主播ID
    @Id
    @NotNull(message = "room_id 不能为空")
    private Long roomId; // 房间号
    @NotNull(message = "title 不能为空")
    private String title; // 直播标题
    @NotNull(message = "description 不能为空")
    private String description; // 直播描述
    @NotNull(message = "liveImage 不能为空")
    private String liveImage; // 直播封面
    @NotNull(message = "rtmp_url 不能为空")
    private String rtmpUrl; // 推流地址
    private Integer isLiveStream; // 是否直播账号
    // 将 Live 对象转换为 JSON 字符串的方法
    public String toJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

