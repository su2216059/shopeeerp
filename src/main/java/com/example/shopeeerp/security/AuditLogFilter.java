package com.example.shopeeerp.security;

import com.example.shopeeerp.pojo.AuditLog;
import com.example.shopeeerp.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuditLogFilter extends OncePerRequestFilter {

    private static final int MAX_BODY_LENGTH = 4000;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public AuditLogFilter(AuditLogService auditLogService, ObjectMapper objectMapper) {
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) {
            return true;
        }
        return path.startsWith("/actuator")
                || path.startsWith("/error")
                || path.startsWith("/api/auth/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        Exception error = null;
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (Exception ex) {
            error = ex;
            throw ex;
        } finally {
            try {
                saveAuditLog(requestWrapper, responseWrapper, error);
            } catch (Exception ignored) {
            }
            responseWrapper.copyBodyToResponse();
        }
    }

    private void saveAuditLog(ContentCachingRequestWrapper request,
                              ContentCachingResponseWrapper response,
                              Exception error) {
        AuditLog log = new AuditLog();
        JwtUserPrincipal principal = SecurityUtil.getCurrentUser();
        if (principal != null) {
            log.setUserId(principal.getUserId());
            log.setUsername(principal.getUsername());
        }

        String method = request.getMethod();
        String path = request.getRequestURI();
        log.setAction(method != null ? method : "UNKNOWN");
        log.setResource(path);
        log.setMethod(method);
        log.setPath(path);
        log.setRequestId(resolveRequestId(request));
        log.setIp(resolveClientIp(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setStatus(resolveStatus(response, error));
        log.setErrorMessage(resolveErrorMessage(response, error));
        log.setBeforeData(null);
        log.setAfterData(buildAfterData(request));
        log.setCreatedAt(LocalDateTime.now());

        auditLogService.save(log);
    }

    private String buildAfterData(ContentCachingRequestWrapper request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("query", request.getQueryString());
        payload.put("params", request.getParameterMap());
        payload.put("body", readRequestBody(request));
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return null;
        }
    }

    private String readRequestBody(ContentCachingRequestWrapper request) {
        String contentType = request.getContentType();
        if (contentType != null && contentType.startsWith("multipart/")) {
            return null;
        }
        byte[] buf = request.getContentAsByteArray();
        if (buf == null || buf.length == 0) {
            return null;
        }
        String charset = request.getCharacterEncoding();
        String body = new String(buf, charset != null ? java.nio.charset.Charset.forName(charset) : StandardCharsets.UTF_8);
        if (body.length() > MAX_BODY_LENGTH) {
            return body.substring(0, MAX_BODY_LENGTH) + "...";
        }
        return body;
    }

    private String resolveStatus(ContentCachingResponseWrapper response, Exception error) {
        if (error != null) {
            return "ERROR";
        }
        int status = response.getStatus();
        return status >= 400 ? "FAILED" : "SUCCESS";
    }

    private String resolveErrorMessage(ContentCachingResponseWrapper response, Exception error) {
        if (error != null) {
            return error.getMessage();
        }
        int status = response.getStatus();
        return status >= 400 ? "HTTP " + status : null;
    }

    private String resolveRequestId(HttpServletRequest request) {
        String header = request.getHeader("X-Request-Id");
        if (header != null && !header.trim().isEmpty()) {
            return header.trim();
        }
        return UUID.randomUUID().toString();
    }

    private String resolveClientIp(HttpServletRequest request) {
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
