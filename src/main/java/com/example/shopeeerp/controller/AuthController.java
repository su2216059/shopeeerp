package com.example.shopeeerp.controller;

import com.example.shopeeerp.mapper.UserMapper;
import com.example.shopeeerp.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    // 简单的 token 存储 (生产环境应使用 Redis 或 JWT)
    private static final Map<String, Long> tokenStore = new HashMap<>();

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> result = new HashMap<>();

        if (request.getUsername() == null || request.getPassword() == null) {
            result.put("success", false);
            result.put("message", "用户名和密码不能为空");
            return ResponseEntity.badRequest().body(result);
        }

        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return ResponseEntity.badRequest().body(result);
        }

        // 简单密码验证 (生产环境应使用 BCrypt)
        if (!request.getPassword().equals(user.getPassword())) {
            result.put("success", false);
            result.put("message", "密码错误");
            return ResponseEntity.badRequest().body(result);
        }

        // 生成 token
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user.getUserId());

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("token", token);
        result.put("user", buildUserInfo(user));

        return ResponseEntity.ok(result);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 验证参数
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "用户名不能为空");
            return ResponseEntity.badRequest().body(result);
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            result.put("success", false);
            result.put("message", "密码长度至少6位");
            return ResponseEntity.badRequest().body(result);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            result.put("success", false);
            result.put("message", "两次密码输入不一致");
            return ResponseEntity.badRequest().body(result);
        }

        // 检查用户名是否已存在
        User existing = userMapper.selectByUsername(request.getUsername());
        if (existing != null) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(result);
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(request.getPassword()); // 生产环境应加密
        user.setRoleId(2L); // 默认普通用户角色
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);

        // 自动登录
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user.getUserId());

        result.put("success", true);
        result.put("message", "注册成功");
        result.put("token", token);
        result.put("user", buildUserInfo(user));

        return ResponseEntity.ok(result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> result = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            result.put("success", false);
            result.put("message", "未登录");
            return ResponseEntity.status(401).body(result);
        }

        String token = authHeader.substring(7);
        Long userId = tokenStore.get(token);

        if (userId == null) {
            result.put("success", false);
            result.put("message", "token无效或已过期");
            return ResponseEntity.status(401).body(result);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return ResponseEntity.status(401).body(result);
        }

        result.put("success", true);
        result.put("user", buildUserInfo(user));

        return ResponseEntity.ok(result);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> result = new HashMap<>();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenStore.remove(token);
        }

        result.put("success", true);
        result.put("message", "已退出登录");

        return ResponseEntity.ok(result);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ChangePasswordRequest request) {
        
        Map<String, Object> result = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            result.put("success", false);
            result.put("message", "未登录");
            return ResponseEntity.status(401).body(result);
        }

        String token = authHeader.substring(7);
        Long userId = tokenStore.get(token);

        if (userId == null) {
            result.put("success", false);
            result.put("message", "token无效");
            return ResponseEntity.status(401).body(result);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return ResponseEntity.status(401).body(result);
        }

        // 验证旧密码
        if (!request.getOldPassword().equals(user.getPassword())) {
            result.put("success", false);
            result.put("message", "原密码错误");
            return ResponseEntity.badRequest().body(result);
        }

        // 验证新密码
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            result.put("success", false);
            result.put("message", "新密码长度至少6位");
            return ResponseEntity.badRequest().body(result);
        }

        // 更新密码
        user.setPassword(request.getNewPassword());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);

        result.put("success", true);
        result.put("message", "密码修改成功");

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

    // Request classes
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
}
