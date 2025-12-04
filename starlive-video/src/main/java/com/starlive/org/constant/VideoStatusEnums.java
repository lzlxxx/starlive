package com.starlive.org.constant;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VideoStatusEnums {
    /**
     * 已发布
     */
    POSTED(0),
    /**
     * 已删除
     */
    DELETE(1),
    /**
     * 审核中
     */
    REVIEW(2),
    /**
     * 隐私视频
     */
    PRIVATE(3);
    private final Integer code;
}
