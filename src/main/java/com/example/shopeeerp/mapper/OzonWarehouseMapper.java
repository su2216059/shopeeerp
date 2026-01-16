package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonWarehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OzonWarehouseMapper {

    OzonWarehouse selectByWarehouseId(@Param("warehouseId") Long warehouseId);

    List<OzonWarehouse> selectAll();

    int insert(OzonWarehouse warehouse);

    int updateByWarehouseId(OzonWarehouse warehouse);

    int deleteByWarehouseId(@Param("warehouseId") Long warehouseId);
}