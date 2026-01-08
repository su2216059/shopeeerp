package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {
    int insert(Role role);
    int deleteById(Long roleId);
    int update(Role role);
    Role selectById(Long roleId);
    List<Role> selectAll();
}
