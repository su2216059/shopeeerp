package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonOrderItem;

import java.util.List;

/**
 * Ozon 订单项服务接口
 */
public interface OzonOrderItemService {

    List<OzonOrderItem> getByOrderId(Long orderId);

    boolean saveBatch(List<OzonOrderItem> items);

    boolean deleteByOrderId(Long orderId);

    boolean deleteAll();

    /**
     * 替换某个订单的全部订单项
     */
    boolean replaceItems(Long orderId, List<OzonOrderItem> items);
}
