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
     * 根据ID查询状态
     */
    OzonProductStatus selectById(@Param("id") Long id);

    /**
     * 根据商品ID查询状态
     */
    OzonProductStatus selectByProductId(@Param("productId") Long productId);

    /**
     * 查询所有状态
     */
    List<OzonProductStatus> selectAll();

    /**
     * 根据条件查询状态列表
     */
    List<OzonProductStatus> selectByCondition(OzonProductStatus condition);

    /**
     * 根据状态查询商品列表
     */
    List<OzonProductStatus> selectByStatus(@Param("status") String status);

    /**
     * 根据审核状态查询商品列表
     */
    List<OzonProductStatus> selectByModerateStatus(@Param("moderateStatus") String moderateStatus);

    /**
     * 插入状态
     */
    int insert(OzonProductStatus status);

    /**
     * 根据ID更新状态
     */
    int updateById(OzonProductStatus status);

    /**
     * 根据商品ID更新状态
     */
    int updateByProductId(OzonProductStatus status);

    /**
     * 根据ID删除状态
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据商品ID删除状态
     */
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 批量删除状态
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 统计状态数量
     */
    long count();

    /**
     * 根据状态统计数量
     */
    long countByStatus(@Param("status") String status);
}