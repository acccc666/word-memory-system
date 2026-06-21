package com.word.wordmemory.common.result;

/**
 * 统一错误码枚举
 * 枚举方式集中管理所有错误码，类型安全，方便扩展。
 * code 规则：200=成功  400=客户端错误  500=服务端错误
 */
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    LOGIN_ERROR(400, "用户名或密码错误"),
    USER_NOT_FOUND(400, "用户不存在"),
    PASSWORD_ERROR(400, "用户名或密码错误"),
    USERNAME_EXISTS(400, "用户名已存在"),
    PARAM_ERROR(400, "请求参数错误"),
    SERVER_ERROR(500, "服务器异常");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() { return code; }
    public String getMsg() { return msg; }
}
