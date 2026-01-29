package com.example.shopeeerp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtUserPrincipal {
    private final Long userId;
    private final String username;
    private final Long roleId;
    private final List<String> permissions;
    private final List<GrantedAuthority> authorities;

    public JwtUserPrincipal(Long userId, String username, Long roleId, List<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.roleId = roleId;
        this.permissions = permissions == null ? new ArrayList<>() : new ArrayList<>(permissions);
        this.authorities = buildAuthorities(roleId, this.permissions);
    }

    private List<GrantedAuthority> buildAuthorities(Long roleId, List<String> permissions) {
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (roleId != null && roleId == 1L) {
            list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        if (permissions != null) {
            for (String permission : permissions) {
                if (permission != null && !permission.trim().isEmpty()) {
                    list.add(new SimpleGrantedAuthority(permission.trim()));
                }
            }
        }
        return list;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Long getRoleId() {
        return roleId;
    }

    public List<String> getPermissions() {
        return new ArrayList<>(permissions);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
