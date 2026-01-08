package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper {
    /**
     * 插入角色记录
     */
    int insert(Role role);

    /**
     * 根据ID删除角色记录
     */
    int deleteById(Long roleId);

    /**
     * 更新角色记录
     */
    int update(Role role);

    /**
     * 根据ID查询角色记录
     */
    Role selectById(Long roleId);

    /**
     * 查询所有角色记录
     */
    List<Role> selectAll();
}
