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
    int deleteById(Long invoiceId);

    /**
     * 更新发票记录
     */
    int update(Invoice invoice);

    /**
     * 根据ID查询发票记录
     */
    Invoice selectById(Long invoiceId);

    /**
     * 查询所有发票记录
     */
    List<Invoice> selectAll();

    /**
     * 根据订单ID查询发票记录
     */
    List<Invoice> selectByOrderId(@Param("orderId") Long orderId);
}
