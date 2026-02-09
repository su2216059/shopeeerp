package com.example.shopeeerp.exception;

public class BizException extends RuntimeException {
    public BizException(String msg) {
        super(msg);
    }

    public BizException(String msg, Throwable e) {
        super(msg, e);
    }
}
