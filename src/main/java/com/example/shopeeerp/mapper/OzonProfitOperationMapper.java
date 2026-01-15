package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonProfitOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OzonProfitOperationMapper {

    OzonProfitOperation selectByOperationId(@Param("operationId") Long operationId);

    List<OzonProfitOperation> selectByPostingNumbers(@Param("postingNumbers") List<String> postingNumbers);

    int insertBatch(@Param("list") List<OzonProfitOperation> list);

    int deleteByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    int deleteByPostingNumber(@Param("postingNumber") String postingNumber);
}
