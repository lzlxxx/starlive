package com.starlive.org.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class LikeAndFollowResult {
    private Object likeCount;//点赞 关注 总数
    private boolean status;//用户的点赞 关注状态
}
