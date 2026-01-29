package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.InvoiceMapper;
import com.example.shopeeerp.pojo.Invoice;
import com.example.shopeeerp.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发票服务实现类
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Override
    public int insert(Invoice invoice) {
        return invoiceMapper.insert(invoice);
    }

    @Override
    public int deleteById(Long invoiceId, Long shopId) {
        return invoiceMapper.deleteById(invoiceId, shopId);
    }

    @Override
    public int update(Invoice invoice) {
        return invoiceMapper.update(invoice);
    }

    @Override
    public Invoice selectById(Long invoiceId, Long shopId) {
        return invoiceMapper.selectById(invoiceId, shopId);
    }

    @Override
    public List<Invoice> selectAll(Long shopId) {
        return invoiceMapper.selectAll(shopId);
    }

    @Override
    public List<Invoice> selectByOrderId(Long orderId, Long shopId) {
        return invoiceMapper.selectByOrderId(orderId, shopId);
    }
}
