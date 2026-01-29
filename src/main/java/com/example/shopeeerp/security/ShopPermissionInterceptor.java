package com.example.shopeeerp.security;

import com.example.shopeeerp.service.UserShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class ShopPermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserShopService userShopService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod method = (HandlerMethod) handler;
        ShopPermission annotation = method.getMethodAnnotation(ShopPermission.class);
        if (annotation == null) {
            return true;
        }

        Long shopId = resolveShopId(request, annotation.param());
        if (shopId == null) {
            throw new AccessDeniedException("Missing shop permission");
        }

        if (SecurityUtil.isAdmin()) {
            return true;
        }

        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null || !userShopService.hasShopAccess(userId, shopId)) {
            throw new AccessDeniedException("Shop access denied");
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private Long resolveShopId(HttpServletRequest request, String paramName) {
        Object attr = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attr instanceof Map) {
            Map<String, String> pathVars = (Map<String, String>) attr;
            String value = pathVars.get(paramName);
            Long parsed = parseLong(value);
            if (parsed != null) {
                return parsed;
            }
        }

        String paramValue = request.getParameter(paramName);
        return parseLong(paramValue);
    }

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
