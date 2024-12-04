package com.starlive.org.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public  class CollectionFolderInfo {
        private Long collectionFolderId;//收藏夹的唯一标识符
        private String collectionFolderName;//收藏夹的名称
        private int collectionCount;//收藏夹中的收藏内容数量
    }

