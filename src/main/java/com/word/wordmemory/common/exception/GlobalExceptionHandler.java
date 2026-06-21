package com.word.wordmemory.common.exception;

import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.common.result.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @RestControllerAdvice 对所有 Controller 生效，自动将返回值转 JSON。
 * Controller/Service 只管 throw 异常，这里统一拦截包装成 Result 格式。
 *
 * 三层处理（按优先级）：
 *   ① BusinessException        → 业务错误，返回自定义 code
 *   ② IllegalArgumentException → 参数错误，返回 400
 *   ③ Exception（兜底）         → 系统错误，返回 500，不暴露内部细节
 */
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
        e.printStackTrace();  // 控制台打印完整错误栈，不返回给前端
        return Result.fail(ResultCode.SERVER_ERROR.getCode(),
                           ResultCode.SERVER_ERROR.getMsg());
    }
}
