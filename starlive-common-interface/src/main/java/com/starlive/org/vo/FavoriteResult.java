package com.starlive.org.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResult {
    private Long collectionFolderId;//收藏内容所在收藏夹的 ID
    private String collectionFolderName;//收藏夹的名称
    private int collectionCount;//当前内容的总收藏次数

    private String action;//操作类型，'added' 表示新增收藏，'removed' 表示取消收藏
    private boolean status;//当前内容的收藏状态，true 已收藏，false 未收藏
}



