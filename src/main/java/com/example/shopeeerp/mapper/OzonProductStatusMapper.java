package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonProductStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Ozon商品状态Mapper接口
 */
@Mapper
public interface OzonProductStatusMapper {
    /**
     * 插入状态记录
     */
    int insert(OzonProductStatus status);

    /**
     * 根据ID删除状态记录
     */
    int deleteById(Long id);

    /**
     * 根据商品ID删除状态记录
     */
    int deleteByProductId(Long productId);

    /**
     * 更新状态记录
     */
    int update(OzonProductStatus status);

    /**
     * 插入或更新状态记录
     */
    int insertOrUpdate(OzonProductStatus status);

    /**
     * 根据ID查询状态记录
     */
    OzonProductStatus selectById(Long id);

    /**
     * 根据商品ID查询状态记录
     */
    OzonProductStatus selectByProductId(Long productId);

    /**
     * 根据状态查询记录
     */
    List<OzonProductStatus> selectByStatus(@Param("status") String status);

    /**
     * 查询所有状态记录
     */
    List<OzonProductStatus> selectAll();
}
