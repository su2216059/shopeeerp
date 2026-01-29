package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {
    List<Permission> selectAll();

    List<String> selectCodesByRoleId(@Param("roleId") Long roleId);
}
