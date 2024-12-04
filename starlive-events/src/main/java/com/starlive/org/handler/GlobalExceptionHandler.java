package com.starlive.org.handler;

import com.starlive.org.exception.ServiceException;
import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public WebResult<Object> handleBusinessException(ServiceException e) {
        return WebResultUtil.failure(e);
    }

    @ExceptionHandler(Exception.class)
    public  WebResult<Object> handleException(Exception e) {
        return WebResultUtil.failure("500", "服务器内部错误");
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public WebResult<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        return  WebResultUtil.failure("400","参数校验失败",errorMessages);
    }
}