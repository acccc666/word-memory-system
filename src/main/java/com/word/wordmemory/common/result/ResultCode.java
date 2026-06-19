package com.word.wordmemory.common.result;

public enum ResultCode {
    // 通用成功
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