package com.word.wordmemory.common.exception;

/**
 * 业务异常 —— Service 层主动抛出的业务错误
 *
 * 继承 RuntimeException（非受检异常），好处：
 *   - 方法可不用 throws 声明，调用链无需 try-catch
 *   - Spring 事务默认对 RuntimeException 回滚
 *
 * 携带 code（错误码）和 message（错误描述），
 * 由 GlobalExceptionHandler 捕获后包装为 Result 返回前端。
 */
public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() { return code; }
}
