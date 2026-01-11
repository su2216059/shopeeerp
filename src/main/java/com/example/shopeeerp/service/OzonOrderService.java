package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonOrder;

import java.util.List;

/**
 * Ozon 订单服务接口
 */
public interface OzonOrderService {

    List<OzonOrder> getAll();

    OzonOrder getById(Long id);

    boolean save(OzonOrder order);

    boolean saveBatch(List<OzonOrder> orders);

    boolean update(OzonOrder order);

    boolean removeById(Long id);

    /**
     * 删除现有订单后重新写入
     */
    boolean replaceAll(List<OzonOrder> orders);

    boolean removeAll();
}
