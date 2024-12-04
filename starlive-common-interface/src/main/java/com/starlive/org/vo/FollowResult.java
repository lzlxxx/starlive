package com.starlive.org.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FollowResult{
    private Object  count;//点赞 关注 总数
    private boolean status;//用户的点赞 关注状态
}
