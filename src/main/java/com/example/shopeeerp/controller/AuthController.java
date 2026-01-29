package com.example.shopeeerp.controller;

import com.example.shopeeerp.mapper.UserMapper;
import com.example.shopeeerp.pojo.User;
import com.example.shopeeerp.security.JwtTokenProvider;
import com.example.shopeeerp.security.JwtUserPrincipal;
import com.example.shopeeerp.service.PermissionService;
import com.example.shopeeerp.service.UserShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
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

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> result = new HashMap<>();

        if (request.getUsername() == null || request.getPassword() == null) {
            result.put("success", false);
            result.put("message", "Username and password are required");
            return ResponseEntity.badRequest().body(result);
        }

        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            result.put("success", false);
            result.put("message", "User not found");
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

        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody RefreshRequest request) {
        Map<String, Object> result = new HashMap<>();

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
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> result = new HashMap<>();

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
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Map<String, Object> result = new HashMap<>();

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
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Logged out");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordRequest request) {
        Map<String, Object> result = new HashMap<>();

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
