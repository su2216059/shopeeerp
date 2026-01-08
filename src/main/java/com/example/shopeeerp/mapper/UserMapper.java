package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    /**
     * 插入用户记录
     */
    int insert(User user);

    /**
     * 根据ID删除用户记录
     */
    int deleteById(Long userId);

    /**
     * 更新用户记录
     */
    int update(User user);

    /**
     * 根据ID查询用户记录
     */
    User selectById(Long userId);

    /**
     * 查询所有用户记录
     */
    List<User> selectAll();

    /**
     * 根据用户名查询用户记录
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据角色ID查询用户记录
     */
    List<User> selectByRoleId(@Param("roleId") Long roleId);
}
