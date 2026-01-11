package com.example.shopeeerp.controller;

import com.example.shopeeerp.adapter.PlatformAdapter;
import com.example.shopeeerp.adapter.PlatformAdapterFactory;
import com.example.shopeeerp.adapter.model.PlatformOrder;
import com.example.shopeeerp.adapter.model.PlatformOrderItem;
import com.example.shopeeerp.pojo.OzonOrder;
import com.example.shopeeerp.pojo.OzonOrderItem;
import com.example.shopeeerp.service.OzonOrderItemService;
import com.example.shopeeerp.service.OzonOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ozon/orders")
@CrossOrigin(origins = "*")
public class OzonOrderController {

    private final OzonOrderService ozonOrderService;
    private final OzonOrderItemService ozonOrderItemService;
    private final PlatformAdapterFactory platformAdapterFactory;
    private final AtomicBoolean syncing = new AtomicBoolean(false);
    private final Executor syncExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    public OzonOrderController(OzonOrderService ozonOrderService,
                               OzonOrderItemService ozonOrderItemService,
                               PlatformAdapterFactory platformAdapterFactory) {
        this.ozonOrderService = ozonOrderService;
        this.ozonOrderItemService = ozonOrderItemService;
        this.platformAdapterFactory = platformAdapterFactory;
    }

    @GetMapping
    public ResponseEntity<List<OzonOrderView>> list() {
        List<OzonOrder> orders = ozonOrderService.getAll();
        List<OzonOrderView> result = orders.stream()
                .map(this::buildView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncOrders() {
        Map<String, Object> result = new HashMap<>();
        if (!syncing.compareAndSet(false, true)) {
            result.put("success", false);
            result.put("message", "同步进行中，请稍后再试");
            return ResponseEntity.status(429).body(result);
        }

        try {
            PlatformAdapter adapter = platformAdapterFactory.getAdapter("ozon");
            CompletableFuture.runAsync(() -> {
                try {
                    performSync(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    syncing.set(false);
                }
            }, syncExecutor);

            result.put("success", true);
            result.put("message", "同步任务已启动，请稍后刷新列表");
            return ResponseEntity.accepted().body(result);
        } catch (Exception e) {
            syncing.set(false);
            result.put("success", false);
            result.put("message", "Sync failed to start");
            return ResponseEntity.internalServerError().body(result);
        }
    }

    private void performSync(PlatformAdapter adapter) {
        List<PlatformOrder> platformOrders = adapter.fetchOrders(null, null);
        if (platformOrders == null) {
            platformOrders = Collections.emptyList();
        }
        Map<Long, List<OzonOrderItem>> itemsByOrder = new HashMap<>();
        List<OzonOrder> orders = convertOrders(platformOrders, itemsByOrder);
        if (orders.isEmpty()) {
            return;
        }
        ozonOrderItemService.deleteAll();
        ozonOrderService.replaceAll(orders);
        itemsByOrder.forEach((orderId, items) -> {
            if (items != null && !items.isEmpty()) {
                ozonOrderItemService.replaceItems(orderId, items);
            }
        });
    }

    private List<OzonOrder> convertOrders(List<PlatformOrder> platformOrders,
                                          Map<Long, List<OzonOrderItem>> itemsByOrder) {
        List<OzonOrder> orders = new ArrayList<>();
        AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());
        LocalDateTime now = LocalDateTime.now();

        for (PlatformOrder platformOrder : platformOrders) {
            if (platformOrder == null) {
                continue;
            }
            Long orderId = parseLong(platformOrder.getPlatformOrderId());
            if (orderId == null) {
                orderId = parseLong(platformOrder.getOrderId());
            }
            if (orderId == null) {
                orderId = idGenerator.getAndIncrement();
            }

            OzonOrder order = new OzonOrder();
            order.setId(orderId);
            order.setOrderNumber(platformOrder.getPlatformOrderId());
            order.setShipmentNumber(platformOrder.getOrderId());
            order.setStore("Ozon");
            order.setAmount(platformOrder.getTotalAmount());
            order.setShipStatus(platformOrder.getShippingStatus());
            order.setStatus(platformOrder.getStatus());
            order.setSubStatus(platformOrder.getPaymentStatus());
            order.setOrderTime(platformOrder.getOrderDate());
            order.setQuantity(sumQuantity(platformOrder.getItems()));
            order.setDetail(buildDetail(platformOrder.getItems()));
            order.setCreatedAt(now);
            order.setUpdatedAt(now);
            order.setSyncTime(now);
            orders.add(order);

            List<OzonOrderItem> items = convertItems(platformOrder.getItems(), orderId, now);
            if (!items.isEmpty()) {
                itemsByOrder.put(orderId, items);
            }
        }
        return orders;
    }

    private List<OzonOrderItem> convertItems(List<PlatformOrderItem> platformItems, Long orderId, LocalDateTime now) {
        if (platformItems == null || platformItems.isEmpty()) {
            return Collections.emptyList();
        }
        List<OzonOrderItem> items = new ArrayList<>();
        for (PlatformOrderItem platformItem : platformItems) {
            if (platformItem == null) {
                continue;
            }
            OzonOrderItem item = new OzonOrderItem();
            item.setOrderId(orderId);
            item.setProductId(parseLong(platformItem.getProductId()));
            item.setOfferId(platformItem.getProductSku());
            item.setName(platformItem.getProductName());
            item.setQuantity(platformItem.getQuantity());
            item.setPrice(platformItem.getUnitPrice());
            item.setCost(null);
            item.setProfit(null);
            if (platformItem.getTotalPrice() != null && platformItem.getUnitPrice() != null) {
                item.setTax(platformItem.getTotalPrice().subtract(platformItem.getUnitPrice()));
            } else {
                item.setTax(null);
            }
            item.setCreatedAt(now);
            item.setUpdatedAt(now);
            items.add(item);
        }
        return items;
    }

    private Integer sumQuantity(List<PlatformOrderItem> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        return items.stream()
                .filter(Objects::nonNull)
                .map(PlatformOrderItem::getQuantity)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
    }

    private String buildDetail(List<PlatformOrderItem> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        String detail = items.stream()
                .filter(Objects::nonNull)
                .map(PlatformOrderItem::getProductName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
        return detail.isEmpty() ? null : detail;
    }

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private OzonOrderView buildView(OzonOrder order) {
        OzonOrderView view = new OzonOrderView();
        view.setId(order.getId());
        view.setOrderNumber(order.getOrderNumber());
        view.setShipmentNumber(order.getShipmentNumber());
        view.setImageUrl(order.getImageUrl());
        view.setDetail(order.getDetail());
        view.setStore(order.getStore());
        view.setAmount(order.getAmount());
        view.setOzonTax(order.getOzonTax());
        view.setCost(order.getCost());
        view.setProfit(order.getProfit());
        view.setPackageInfo(order.getPackageInfo());
        view.setQuantity(order.getQuantity());
        view.setPurchaseNo(order.getPurchaseNo());
        view.setDomesticNo(order.getDomesticNo());
        view.setWeight(order.getWeight());
        view.setTrackingNo(order.getTrackingNo());
        view.setLogisticsType(order.getLogisticsType());
        view.setShipStatus(order.getShipStatus());
        view.setStatus(order.getStatus());
        view.setSubStatus(order.getSubStatus());
        view.setOrderTime(order.getOrderTime());
        view.setCurrency(order.getCurrency());
        return view;
    }

    public static class OzonOrderView {
        private Long id;
        private String orderNumber;
        private String shipmentNumber;
        private String imageUrl;
        private String detail;
        private String store;
        private BigDecimal amount;
        private BigDecimal ozonTax;
        private BigDecimal cost;
        private BigDecimal profit;
        private String packageInfo;
        private Integer quantity;
        private String purchaseNo;
        private String domesticNo;
        private BigDecimal weight;
        private String trackingNo;
        private String logisticsType;
        private String shipStatus;
        private String status;
        private String subStatus;
        private LocalDateTime orderTime;
        private String currency;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public String getShipmentNumber() {
            return shipmentNumber;
        }

        public void setShipmentNumber(String shipmentNumber) {
            this.shipmentNumber = shipmentNumber;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getStore() {
            return store;
        }

        public void setStore(String store) {
            this.store = store;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getOzonTax() {
            return ozonTax;
        }

        public void setOzonTax(BigDecimal ozonTax) {
            this.ozonTax = ozonTax;
        }

        public BigDecimal getCost() {
            return cost;
        }

        public void setCost(BigDecimal cost) {
            this.cost = cost;
        }

        public BigDecimal getProfit() {
            return profit;
        }

        public void setProfit(BigDecimal profit) {
            this.profit = profit;
        }

        public String getPackageInfo() {
            return packageInfo;
        }

        public void setPackageInfo(String packageInfo) {
            this.packageInfo = packageInfo;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getPurchaseNo() {
            return purchaseNo;
        }

        public void setPurchaseNo(String purchaseNo) {
            this.purchaseNo = purchaseNo;
        }

        public String getDomesticNo() {
            return domesticNo;
        }

        public void setDomesticNo(String domesticNo) {
            this.domesticNo = domesticNo;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public String getTrackingNo() {
            return trackingNo;
        }

        public void setTrackingNo(String trackingNo) {
            this.trackingNo = trackingNo;
        }

        public String getLogisticsType() {
            return logisticsType;
        }

        public void setLogisticsType(String logisticsType) {
            this.logisticsType = logisticsType;
        }

        public String getShipStatus() {
            return shipStatus;
        }

        public void setShipStatus(String shipStatus) {
            this.shipStatus = shipStatus;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubStatus() {
            return subStatus;
        }

        public void setSubStatus(String subStatus) {
            this.subStatus = subStatus;
        }

        public LocalDateTime getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(LocalDateTime orderTime) {
            this.orderTime = orderTime;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}
