package com.word.wordmemory.common.result;

/**
 * 统一 API 响应结果
 *
 * @param <T> data 字段的实际类型
 * 前端统一按 code 判断：200=成功取data，400=业务错误取msg，500=系统异常
 */
public class Result<T> {
    private Integer code;  // 200=成功  400=业务/参数错误  500=系统异常
    private String msg;    // 提示信息
    private T data;        // 返回数据（失败时为 null）

    public static <T> Result<T> success() {
        Result<T> r = new Result<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMsg());
        return r;
    }

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMsg());
        r.setData(data);
        return r;
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static <T> Result<T> fail() {
        Result<T> r = new Result<>();
        r.setCode(ResultCode.SERVER_ERROR.getCode());
        r.setMsg(ResultCode.SERVER_ERROR.getMsg());
        return r;
    }

    /** 使用默认 500 错误码，自定义错误信息 */
    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode(ResultCode.SERVER_ERROR.getCode());
        r.setMsg(msg);
        return r;
    }

    // Getter / Setter
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
