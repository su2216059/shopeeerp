package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.Warehouse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 仓库Mapper接口
 */
@Mapper
public interface WarehouseMapper {
    /**
     * 插入仓库记录
     */
    int insert(Warehouse warehouse);

    /**
     * 根据ID删除仓库记录
     */
    int deleteById(Long warehouseId);

    /**
     * 更新仓库记录
     */
    int update(Warehouse warehouse);

    /**
     * 根据ID查询仓库记录
     */
    Warehouse selectById(Long warehouseId);

    /**
     * 查询所有仓库记录
     */
    List<Warehouse> selectAll();
}
