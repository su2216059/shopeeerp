package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.AuditLogMapper;
import com.example.shopeeerp.pojo.AuditLog;
import com.example.shopeeerp.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AuditLogServiceImpl(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    public void save(AuditLog auditLog) {
        if (auditLog == null) {
            return;
        }
        if (auditLog.getCreatedAt() == null) {
            auditLog.setCreatedAt(LocalDateTime.now());
        }
        auditLogMapper.insert(auditLog);
    }

    @Override
    public void logLogin(Long userId,
                         String username,
                         boolean success,
                         String message,
                         HttpServletRequest request) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction("LOGIN");
        log.setResource("auth");
        log.setMethod(request != null ? request.getMethod() : "POST");
        log.setPath(request != null ? request.getRequestURI() : "/api/auth/login");
        log.setRequestId(resolveRequestId(request));
        log.setIp(resolveClientIp(request));
        log.setUserAgent(request != null ? request.getHeader("User-Agent") : null);
        log.setStatus(success ? "SUCCESS" : "FAILED");
        log.setErrorMessage(success ? null : message);
        log.setBeforeData(null);
        log.setAfterData(buildLoginPayload(username, success, message));
        save(log);
    }

    private String buildLoginPayload(String username, boolean success, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("success", success);
        payload.put("message", message);
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        if (request == null) {
            return UUID.randomUUID().toString();
        }
        String header = request.getHeader("X-Request-Id");
        if (header != null && !header.trim().isEmpty()) {
            return header.trim();
        }
        return UUID.randomUUID().toString();
    }

    private String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.trim().isEmpty()) {
            int comma = forwarded.indexOf(',');
            return comma > 0 ? forwarded.substring(0, comma).trim() : forwarded.trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.trim().isEmpty()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

}
