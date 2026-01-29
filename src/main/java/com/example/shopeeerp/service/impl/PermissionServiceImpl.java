package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.PermissionMapper;
import com.example.shopeeerp.mapper.RolePermissionMapper;
import com.example.shopeeerp.pojo.Permission;
import com.example.shopeeerp.pojo.RolePermission;
import com.example.shopeeerp.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionMapper.selectAll();
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        List<RolePermission> items = rolePermissionMapper.selectByRoleId(roleId);
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPermissionCodesByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        if (roleId == 1L) {
            List<Permission> all = permissionMapper.selectAll();
            return all.stream()
                    .map(Permission::getCode)
                    .collect(Collectors.toList());
        }
        return permissionMapper.selectCodesByRoleId(roleId);
    }

    @Override
    @Transactional
    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
        if (roleId == null) {
            return;
        }
        rolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        Set<Long> distinct = new LinkedHashSet<>();
        for (Long id : permissionIds) {
            if (id != null) {
                distinct.add(id);
            }
        }
        if (distinct.isEmpty()) {
            return;
        }
        for (Long permissionId : distinct) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        }
    }
}
