package com.starlive.org.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

//{
//        "userId": "string",  // 用户ID
//        "authorId": "string", // 被关注ID
//        }

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FollowRequest {
    private String userId;
    private String authorId;
}
