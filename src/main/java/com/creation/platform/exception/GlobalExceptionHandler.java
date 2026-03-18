package com.creation.platform.exception;

import com.creation.platform.entity.Result;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截数据库重复键异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(DuplicateKeyException.class)
    public Result<String> handleDuplicateKeyException(DuplicateKeyException e) {
        return Result.error(500, "数据已存在，请勿重复添加");
    }

    /**
     * 拦截业务 RuntimeException
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        return Result.error(500, e.getMessage());
    }
}
