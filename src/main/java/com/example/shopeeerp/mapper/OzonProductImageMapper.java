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
     * 插入图片记录
     */
    int insert(OzonProductImage image);

    /**
     * 批量插入图片记录
     */
    int batchInsert(@Param("list") List<OzonProductImage> images);

    /**
     * 根据ID删除图片记录
     */
    int deleteById(Long id);

    /**
     * 根据商品ID删除所有图片
     */
    int deleteByProductId(Long productId);

    /**
     * 更新图片记录
     */
    int update(OzonProductImage image);

    /**
     * 根据ID查询图片记录
     */
    OzonProductImage selectById(Long id);

    /**
     * 根据商品ID查询所有图片
     */
    List<OzonProductImage> selectByProductId(Long productId);

    /**
     * 查询所有图片记录
     */
    List<OzonProductImage> selectAll();
}
