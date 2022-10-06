package com.htsing.pos.easyhttp;

import java.io.Serializable;

public class CommonResult<T> implements Serializable {
    /**
     * 成功标志
     */
    private boolean success = false;

    /**
     * 返回处理消息
     */
    private String message = "";

    /**
     * 返回代码
     */
    private Integer code = 0;

    /**
     * 返回数据对象 result
     */
    private T result;

    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code =  code;
    }

    public boolean getSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success =  success;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message =  message;
    }
    public T getResult() {
        return result;
    }
    public void setResult(T result) {
        this.result =  result;
    }

    public static<T> CommonResult<T> ok(T data) {
        CommonResult<T> r = new CommonResult<T>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMessage("成功");
        r.setResult(data);
        return r;
    }
    public static<T> CommonResult<T> error(String msg) {
        return error(500, msg);
    }
    public static<T> CommonResult<T> error(int code, String msg) {
        CommonResult<T> r = new CommonResult<T>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }
}
