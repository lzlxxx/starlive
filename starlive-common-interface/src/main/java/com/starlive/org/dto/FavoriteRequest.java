package com.starlive.org.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRequest {

    private String userId;// 用户ID
    private String contentId;// 视频ID或直播ID
    private String type;// 类型，"video" 或 "live"
    private int action;//1 收藏 0取消收藏
    private String folderId;// 收藏夹ID 新建时可以为空
    private String folderName;// 新建收藏夹时的名称（仅在新建时使用）不传时默认收藏在默认收藏夹

}
