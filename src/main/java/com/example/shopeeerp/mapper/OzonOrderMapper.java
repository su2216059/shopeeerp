package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OzonOrderMapper {
    List<OzonOrder> selectAll();
    int insert(OzonOrder order);
    int update(OzonOrder order);
    int deleteById(Long id);
    OzonOrder selectById(Long id);
    int deleteAll();
}
