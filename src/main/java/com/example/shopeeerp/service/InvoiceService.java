package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.Invoice;

import java.util.List;

/**
 * 发票服务接口
 */
public interface InvoiceService {
    int insert(Invoice invoice);
    int deleteById(Long invoiceId);
    int update(Invoice invoice);
    Invoice selectById(Long invoiceId);
    List<Invoice> selectAll();
    List<Invoice> selectByOrderId(Long orderId);
}
