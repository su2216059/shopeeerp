package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.RoleMapper;
import com.example.shopeeerp.pojo.Role;
import com.example.shopeeerp.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public int insert(Role role) {
        return roleMapper.insert(role);
    }

    @Override
    public int deleteById(Long roleId) {
        return roleMapper.deleteById(roleId);
    }

    @Override
    public int update(Role role) {
        return roleMapper.update(role);
    }

    @Override
    public Role selectById(Long roleId) {
        return roleMapper.selectById(roleId);
    }

    @Override
    public List<Role> selectAll() {
        return roleMapper.selectAll();
    }
}
