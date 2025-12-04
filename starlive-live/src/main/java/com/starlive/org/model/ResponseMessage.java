package com.starlive.org.model;

import com.starlive.org.result.WebResultUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 响应消息模型
 **/

@Data
@AllArgsConstructor
public class ResponseMessage {

    private String message;
    private String code;
}

