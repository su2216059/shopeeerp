package com.example.shopeeerp.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static JwtUserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtUserPrincipal) {
            return (JwtUserPrincipal) principal;
        }
        return null;
    }

    public static Long getCurrentUserId() {
        JwtUserPrincipal principal = getCurrentUser();
        return principal == null ? null : principal.getUserId();
    }

    public static boolean isAdmin() {
        JwtUserPrincipal principal = getCurrentUser();
        return principal != null && principal.getRoleId() != null && principal.getRoleId() == 1L;
    }
}
