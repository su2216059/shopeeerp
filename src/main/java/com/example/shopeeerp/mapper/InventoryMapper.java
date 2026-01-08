package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存Mapper接口
 */
@Mapper
public interface InventoryMapper {
    /**
     * 插入库存记录
     */
    int insert(Inventory inventory);

    /**
     * 根据ID删除库存记录
     */
    int deleteById(Long inventoryId);

    /**
     * 更新库存记录
     */
    int update(Inventory inventory);

    /**
     * 根据ID查询库存记录
     */
    Inventory selectById(Long inventoryId);

    /**
     * 查询所有库存记录
     */
    List<Inventory> selectAll();

    /**
     * 根据产品ID查询库存记录
     */
    List<Inventory> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据仓库ID查询库存记录
     */
    List<Inventory> selectByWarehouseId(@Param("warehouseId") Long warehouseId);
}
