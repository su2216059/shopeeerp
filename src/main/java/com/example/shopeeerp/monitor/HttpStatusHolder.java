package com.example.shopeeerp.monitor;

public final class HttpStatusHolder {
    private static final ThreadLocal<Integer> STATUS = new ThreadLocal<>();

    private HttpStatusHolder() {}

    public static void setStatus(Integer status) {
        STATUS.set(status);
    }

    public static Integer getStatus() {
        return STATUS.get();
    }

    public static void clear() {
        STATUS.remove();
    }
}
