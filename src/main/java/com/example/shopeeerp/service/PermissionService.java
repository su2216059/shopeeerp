package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.Permission;

import java.util.List;

public interface PermissionService {
    List<Permission> getAllPermissions();

    List<Long> getPermissionIdsByRoleId(Long roleId);

    List<String> getPermissionCodesByRoleId(Long roleId);

    void updateRolePermissions(Long roleId, List<Long> permissionIds);
}
