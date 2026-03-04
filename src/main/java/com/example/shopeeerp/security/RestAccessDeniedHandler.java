package com.example.shopeeerp.security;

import com.example.shopeeerp.adapter.dto.ozon.CommonResult;
import com.example.shopeeerp.adapter.dto.ozon.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        CommonResult<Void> body = CommonResult.fail(ResultCode.FORBIDDEN, ResultCode.FORBIDDEN.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
