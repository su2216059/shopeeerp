package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.Invoice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发票Mapper接口
 */
@Mapper
public interface InvoiceMapper {
    /**
     * 插入发票记录
     */
    int insert(Invoice invoice);

    /**
     * 根据ID删除发票记录
     */
    int deleteById(@Param("invoiceId") Long invoiceId, @Param("shopId") Long shopId);

    /**
     * 更新发票记录
     */
    int update(Invoice invoice);

    /**
     * 根据ID查询发票记录
     */
    Invoice selectById(@Param("invoiceId") Long invoiceId, @Param("shopId") Long shopId);

    /**
     * 查询所有发票记录
     */
    List<Invoice> selectAll(@Param("shopId") Long shopId);

    /**
     * 根据订单ID查询发票记录
     */
    List<Invoice> selectByOrderId(@Param("orderId") Long orderId, @Param("shopId") Long shopId);
}
