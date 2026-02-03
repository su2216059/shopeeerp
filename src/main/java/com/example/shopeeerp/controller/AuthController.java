package com.example.shopeeerp.controller;

import com.example.shopeeerp.mapper.UserMapper;
import com.example.shopeeerp.pojo.User;
import com.example.shopeeerp.security.JwtTokenProvider;
import com.example.shopeeerp.security.JwtUserPrincipal;
import com.example.shopeeerp.service.AuditLogService;
import com.example.shopeeerp.service.PermissionService;
import com.example.shopeeerp.service.UserShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@Slf4j
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserShopService userShopService;

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request,
                                                     HttpServletRequest httpRequest) {
        log.info("用户登录: username={}", request != null ? request.getUsername() : null);
        Map<String, Object> result = new HashMap<>();
        try {
            if (request.getUsername() == null || request.getPassword() == null) {
                result.put("success", false);
                result.put("message", "Username and password are required");
                auditLogService.logLogin(null, request.getUsername(), false, "Username and password are required", httpRequest);
                return ResponseEntity.badRequest().body(result);
            }

            User user = userMapper.selectByUsername(request.getUsername());
            if (user == null) {
                result.put("success", false);
                result.put("message", "User not found");
                auditLogService.logLogin(null, request.getUsername(), false, "User not found", httpRequest);
                return ResponseEntity.badRequest().body(result);
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                if (!isBcryptHash(user.getPassword()) && request.getPassword().equals(user.getPassword())) {
                    String encoded = passwordEncoder.encode(request.getPassword());
                    userMapper.updatePasswordById(user.getUserId(), encoded, LocalDateTime.now());
                    user.setPassword(encoded);
                } else {
                    result.put("success", false);
                    result.put("message", "Invalid password");
                    auditLogService.logLogin(user.getUserId(), user.getUsername(), false, "Invalid password", httpRequest);
                    return ResponseEntity.badRequest().body(result);
                }
            }

            Long currentShopId = getExistingShopId(user);
            java.util.List<String> permissions = permissionService.getPermissionCodesByRoleId(user.getRoleId());
            String token = jwtTokenProvider.generateAccessToken(user, permissions);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user, permissions);

            result.put("success", true);
            result.put("message", "Login successful");
            result.put("token", token);
            result.put("refreshToken", refreshToken);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", jwtTokenProvider.getAccessTokenExpiresInSeconds());
            result.put("permissions", permissions);
            result.put("user", buildUserInfo(user));
            if (currentShopId != null) {
                result.put("currentShopId", currentShopId);
            }

            auditLogService.logLogin(user.getUserId(), user.getUsername(), true, "Login successful", httpRequest);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("登录失败: username={}, 原因={}", request != null ? request.getUsername() : null, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("登录异常: username={}", request != null ? request.getUsername() : null, e);
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody RefreshRequest request) {
        log.info("刷新Token: hasRefreshToken={}", request != null && request.getRefreshToken() != null);
        Map<String, Object> result = new HashMap<>();
        try {
            if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "refreshToken is required");
                return ResponseEntity.badRequest().body(result);
            }

            String refreshToken = request.getRefreshToken().trim();
            if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
                result.put("success", false);
                result.put("message", "refreshToken is invalid or expired");
                return ResponseEntity.status(401).body(result);
            }

            Long userId = jwtTokenProvider.getUserId(refreshToken);
            User user = userMapper.selectById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "User not found");
                return ResponseEntity.status(401).body(result);
            }

            java.util.List<String> permissions = permissionService.getPermissionCodesByRoleId(user.getRoleId());
            String newAccessToken = jwtTokenProvider.generateAccessToken(user, permissions);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user, permissions);

            result.put("success", true);
            result.put("token", newAccessToken);
            result.put("refreshToken", newRefreshToken);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", jwtTokenProvider.getAccessTokenExpiresInSeconds());
            result.put("permissions", permissions);
            result.put("user", buildUserInfo(user));

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("刷新Token失败: 原因={}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("刷新Token异常", e);
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        log.info("用户注册: username={}", request != null ? request.getUsername() : null);
        Map<String, Object> result = new HashMap<>();
        try {
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Username is required");
                return ResponseEntity.badRequest().body(result);
            }

            if (request.getPassword() == null || request.getPassword().length() < 6) {
                result.put("success", false);
                result.put("message", "Password must be at least 6 characters");
                return ResponseEntity.badRequest().body(result);
            }

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                result.put("success", false);
                result.put("message", "Passwords do not match");
                return ResponseEntity.badRequest().body(result);
            }

            User existing = userMapper.selectByUsername(request.getUsername());
            if (existing != null) {
                result.put("success", false);
                result.put("message", "Username already exists");
                return ResponseEntity.badRequest().body(result);
            }

            User user = new User();
            user.setUsername(request.getUsername().trim());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRoleId(2L);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            userMapper.insert(user);

            Long currentShopId = getExistingShopId(user);
            java.util.List<String> permissions = permissionService.getPermissionCodesByRoleId(user.getRoleId());
            String token = jwtTokenProvider.generateAccessToken(user, permissions);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user, permissions);

            result.put("success", true);
            result.put("message", "Register successful");
            result.put("token", token);
            result.put("refreshToken", refreshToken);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", jwtTokenProvider.getAccessTokenExpiresInSeconds());
            result.put("permissions", permissions);
            result.put("user", buildUserInfo(user));
            if (currentShopId != null) {
                result.put("currentShopId", currentShopId);
            }

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("注册失败: username={}, 原因={}", request != null ? request.getUsername() : null, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("注册异常: username={}", request != null ? request.getUsername() : null, e);
            throw e;
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        log.info("获取当前用户");
        Map<String, Object> result = new HashMap<>();
        try {
            JwtUserPrincipal principal = getAuthenticatedPrincipal();
            if (principal == null) {
                result.put("success", false);
                result.put("message", "Unauthorized");
                return ResponseEntity.status(401).body(result);
            }

            User user = userMapper.selectById(principal.getUserId());
            if (user == null) {
                result.put("success", false);
                result.put("message", "User not found");
                return ResponseEntity.status(401).body(result);
            }

            result.put("success", true);
            result.put("user", buildUserInfo(user));
            result.put("permissions", permissionService.getPermissionCodesByRoleId(user.getRoleId()));

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("获取当前用户失败: 原因={}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取当前用户异常", e);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        log.info("用户退出登录");
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "Logged out");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("退出失败: 原因={}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("退出异常", e);
            throw e;
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordRequest request) {
        log.info("修改密码");
        Map<String, Object> result = new HashMap<>();
        try {
            JwtUserPrincipal principal = getAuthenticatedPrincipal();
            if (principal == null) {
                result.put("success", false);
                result.put("message", "Unauthorized");
                return ResponseEntity.status(401).body(result);
            }

            User user = userMapper.selectById(principal.getUserId());
            if (user == null) {
                result.put("success", false);
                result.put("message", "User not found");
                return ResponseEntity.status(401).body(result);
            }

            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                result.put("success", false);
                result.put("message", "Old password is incorrect");
                return ResponseEntity.badRequest().body(result);
            }

            if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
                result.put("success", false);
                result.put("message", "New password must be at least 6 characters");
                return ResponseEntity.badRequest().body(result);
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.update(user);

            result.put("success", true);
            result.put("message", "Password updated");

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("修改密码失败: 原因={}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("修改密码异常", e);
            throw e;
        }
    }

    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("userId", user.getUserId());
        info.put("username", user.getUsername());
        info.put("roleId", user.getRoleId());
        info.put("createdAt", user.getCreatedAt());
        return info;
    }

    private JwtUserPrincipal getAuthenticatedPrincipal() {
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

    private boolean isBcryptHash(String value) {
        return value != null && (value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$"));
    }

    private Long getExistingShopId(User user) {
        if (user == null || user.getUserId() == null) {
            return null;
        }
        java.util.List<Long> shopIds = userShopService.getShopIdsByUserId(user.getUserId());
        if (shopIds != null && !shopIds.isEmpty()) {
            return shopIds.get(0);
        }
        return null;
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private String confirmPassword;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }

    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class RefreshRequest {
        private String refreshToken;

        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
}
