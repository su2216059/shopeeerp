package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.Invoice;

import java.util.List;

/**
 * 发票服务接口
 */
public interface InvoiceService {
    int insert(Invoice invoice);
    int deleteById(Long invoiceId, Long shopId);
    int update(Invoice invoice);
    Invoice selectById(Long invoiceId, Long shopId);
    List<Invoice> selectAll(Long shopId);
    List<Invoice> selectByOrderId(Long orderId, Long shopId);
}
