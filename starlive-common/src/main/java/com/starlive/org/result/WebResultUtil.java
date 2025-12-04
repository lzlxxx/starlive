package com.starlive.org.result;


import com.starlive.org.errcode.BaseErrorCode;
import com.starlive.org.exception.AbstractException;

import java.util.Optional;

/**
 * 全局返回对象构造器
 */
public final class WebResultUtil {

    /**
     * 构造成功响应
     */
    public static <T>WebResult<T> success() {
        return new WebResult<T>()
                .setCode(WebResult.SUCCESS_CODE);
    }

    /**
     * 构造带返回数据的成功响应
     */
    public static <T> WebResult<T> success(T data) {
        return new WebResult<T>()
                .setCode(WebResult.SUCCESS_CODE)
                .setResult(data);
    }

    /**
     * 构建服务端失败响应
     */
    public static <T>WebResult<T> failure() {
        return new WebResult<T>()
                .setCode(BaseErrorCode.SERVICE_ERROR.code())
                .setMessage(BaseErrorCode.SERVICE_ERROR.message());
    }

    /**
     * 通过 {@link AbstractException} 构建失败响应
     */
    public static <T> WebResult<T> failure(AbstractException abstractException) {
        String errorCode = Optional.ofNullable(abstractException.getErrorCode())
                .orElse(BaseErrorCode.SERVICE_ERROR.code());
        String errorMessage = Optional.ofNullable(abstractException.getErrorMessage())
                .orElse(BaseErrorCode.SERVICE_ERROR.message());
        return new WebResult<T>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }

    /**
     * 通过 errorCode、errorMessage 构建失败响应，返回指定泛型类型
     */
    public static <T> WebResult<T> failure(String errorCode, String errorMessage) {
        return new WebResult<T>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }
    public static <T> WebResult<T> failure(String errorCode, String errorMessage,T result) {
        return new WebResult<T>()
                .setCode(errorCode)
                .setMessage(errorMessage)
                .setResult(result);
    }

}
