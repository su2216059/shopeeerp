package com.example.shopeeerp.adapter.dto.ozon;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: R
 * @packageName: com.example.shopeeerp.adapter.dto.ozon
 * @description:
 * @date: 2026/2/4 16:37
 */
public class CommonResult<T> {
    private int code;
    private String msg;
    private T data;

    public CommonResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public CommonResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> CommonResult<T> success(T data){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> CommonResult<T> fail(T data){
        return new CommonResult<T>(ResultCode.VALIDATE_FAILED.getCode(),ResultCode.VALIDATE_FAILED.getMessage(), data);
    }

    public static <T> CommonResult<T> fail(int code,String msg){
        return new CommonResult<T>(code,msg);
    }
    public static <T> CommonResult<T> fail(ResultCode resultCode,String msg){
        return new CommonResult<T>(resultCode.getCode(),msg);
    }

}
