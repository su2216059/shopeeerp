package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.UserMapper;
import com.example.shopeeerp.pojo.User;
import com.example.shopeeerp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public int insert(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int deleteById(Long userId) {
        return userMapper.deleteById(userId);
    }

    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    @Override
    public User selectById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    @Override
    public User selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public List<User> selectByRoleId(Long roleId) {
        return userMapper.selectByRoleId(roleId);
    }
}
