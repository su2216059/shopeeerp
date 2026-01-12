package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonPostingItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OzonPostingItemMapper {

    List<OzonPostingItem> selectByPostingNumber(@Param("postingNumber") String postingNumber);

    int insertBatch(@Param("list") List<OzonPostingItem> items);

    int deleteByPostingNumber(@Param("postingNumber") String postingNumber);

    int deleteAll();
}
