package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    int insert(User user);
    int deleteById(Long userId);
    int update(User user);
    User selectById(Long userId);
    List<User> selectAll();
    User selectByUsername(String username);
    List<User> selectByRoleId(Long roleId);
}
