package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonDeliveryMethod;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OzonDeliveryMethodMapper {

    OzonDeliveryMethod selectById(@Param("id") Long id);

    List<OzonDeliveryMethod> selectByWarehouseId(@Param("warehouseId") Long warehouseId);

    List<OzonDeliveryMethod> selectByWarehouseIds(@Param("warehouseIds") List<Long> warehouseIds);

    int insert(OzonDeliveryMethod method);

    int insertBatch(@Param("list") List<OzonDeliveryMethod> list);

    int updateById(OzonDeliveryMethod method);

    int deleteByWarehouseId(@Param("warehouseId") Long warehouseId);

    int deleteByWarehouseIds(@Param("warehouseIds") List<Long> warehouseIds);
}