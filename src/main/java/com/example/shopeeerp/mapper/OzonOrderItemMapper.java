package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonOrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OzonOrderItemMapper {
    List<OzonOrderItem> selectByOrderId(Long orderId);
    int insertBatch(List<OzonOrderItem> items);
    int deleteByOrderId(Long orderId);
    int deleteAll();
}
