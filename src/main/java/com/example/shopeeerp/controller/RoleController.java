package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.Role;
import com.example.shopeeerp.service.PermissionService;
import com.example.shopeeerp.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.selectAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.selectById(id);
        if (role != null) {
            return ResponseEntity.ok(role);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        int result = roleService.insert(role);
        if (result > 0) {
            return ResponseEntity.ok(role);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        role.setRoleId(id);
        int result = roleService.update(role);
        if (result > 0) {
            return ResponseEntity.ok(role);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        int result = roleService.deleteById(id);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<List<Long>> getRolePermissions(@PathVariable Long id) {
        List<Long> permissionIds = permissionService.getPermissionIdsByRoleId(id);
        return ResponseEntity.ok(permissionIds);
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<Map<String, Object>> updateRolePermissions(
            @PathVariable Long id,
            @RequestBody RolePermissionRequest request) {
        List<Long> permissionIds = request != null ? request.getPermissionIds() : Collections.emptyList();
        permissionService.updateRolePermissions(id, permissionIds);
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    public static class RolePermissionRequest {
        private List<Long> permissionIds;

        public List<Long> getPermissionIds() {
            return permissionIds;
        }

        public void setPermissionIds(List<Long> permissionIds) {
            this.permissionIds = permissionIds;
        }
    }
}
