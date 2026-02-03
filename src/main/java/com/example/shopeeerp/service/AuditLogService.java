package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.AuditLog;

import javax.servlet.http.HttpServletRequest;

public interface AuditLogService {
    void save(AuditLog auditLog);

    void logLogin(Long userId,
                  String username,
                  boolean success,
                  String message,
                  HttpServletRequest request);
}
