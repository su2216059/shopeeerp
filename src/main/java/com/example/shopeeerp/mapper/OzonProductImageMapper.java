package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Ozon商品图片Mapper接口
 */
@Mapper
public interface OzonProductImageMapper {

    /**
     * 根据ID查询图片
     */
    OzonProductImage selectById(@Param("id") Long id);

    /**
     * 根据商品ID查询图片列表
     */
    List<OzonProductImage> selectByProductId(@Param("productId") Long productId);

    /**
     * 查询所有图片
     */
    List<OzonProductImage> selectAll();

    /**
     * 根据条件查询图片列表
     */
    List<OzonProductImage> selectByCondition(OzonProductImage condition);

    /**
     * 插入图片
     */
    int insert(OzonProductImage image);

    /**
     * 批量插入图片
     */
    int insertBatch(@Param("list") List<OzonProductImage> images);

    /**
     * 根据ID更新图片
     */
    int updateById(OzonProductImage image);

    /**
     * 根据ID删除图片
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据商品ID删除图片
     */
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 批量删除图片
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 统计图片数量
     */
    long count();

    /**
     * 根据商品ID统计图片数量
     */
    long countByProductId(@Param("productId") Long productId);
}