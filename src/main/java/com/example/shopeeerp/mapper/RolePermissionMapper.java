package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RolePermissionMapper {
    int insert(RolePermission rolePermission);

    int deleteByRoleId(@Param("roleId") Long roleId);

    List<RolePermission> selectByRoleId(@Param("roleId") Long roleId);
}
