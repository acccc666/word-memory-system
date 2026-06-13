package com.word.wordmemory.common.exception;

import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.common.result.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleParamException(IllegalArgumentException e) {
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleSystemException(Exception e) {
        e.printStackTrace();
        return Result.fail(ResultCode.SERVER_ERROR.getCode(), "服务器繁忙，请稍后再试");
    }
}