package com.starlive.org.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//{
//        "userId": "string",  // 用户ID
//        "contentId": "string", // 视频或直播ID
//        "isCancel": "int",     //是否取消   0是1否
//        "num": "int",          //点赞数     直播可以多个   视频默认为1
//        "type": "string"      // 类型，"video" 或 "live"
//        }
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeRequest {
    private String userId;//用户ID
    private String contentId;//视频或直播ID
    private int isCancel;//是否取消   0是1否   0不点赞  1点赞
    private int num;//点赞数     直播可以多个   视频默认为1
    private String type;//类型，"video" 或 "live"
}
