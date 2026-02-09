package com.example.shopeeerp.adapter.dto.ozon;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: ResultCode
 * @packageName: com.example.shopeeerp.adapter.dto.ozon
 * @description:
 * @date: 2026/2/4 16:43
 */
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统繁忙"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或权限不足"),
    FORBIDDEN(403, "没有操作权限");
    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
