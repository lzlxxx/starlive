package com.starlive.org.handler;

import com.starlive.org.exception.ServiceException;
import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public WebResult<Void> handleBusinessException(ServiceException e) {
        return WebResultUtil.failure(e);
    }

    @ExceptionHandler(Exception.class)
    public  WebResult<Void> handleException(Exception e) {
        return WebResultUtil.failure("500", "服务器内部错误");
    }
}