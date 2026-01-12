package com.example.shopeeerp.controller;

import com.example.shopeeerp.adapter.PlatformAdapter;
import com.example.shopeeerp.adapter.PlatformAdapterFactory;
import com.example.shopeeerp.adapter.impl.OzonAdapter;
import com.example.shopeeerp.adapter.dto.ozon.OzonPostingSyncResult;
import com.example.shopeeerp.pojo.OzonPosting;
import com.example.shopeeerp.service.OzonPostingItemService;
import com.example.shopeeerp.service.OzonPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ozon/orders")
@CrossOrigin(origins = "*")
public class OzonOrderController {

    private final OzonPostingService ozonPostingService;
    private final OzonPostingItemService ozonPostingItemService;
    private final PlatformAdapterFactory platformAdapterFactory;
    private final AtomicBoolean syncing = new AtomicBoolean(false);
    private final Executor syncExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    public OzonOrderController(OzonPostingService ozonPostingService,
                               OzonPostingItemService ozonPostingItemService,
                               PlatformAdapterFactory platformAdapterFactory) {
        this.ozonPostingService = ozonPostingService;
        this.ozonPostingItemService = ozonPostingItemService;
        this.platformAdapterFactory = platformAdapterFactory;
    }

    @GetMapping
    public ResponseEntity<List<OzonPostingView>> list() {
        List<OzonPosting> postings = ozonPostingService.getAll();
        List<OzonPostingView> result = postings.stream()
                .map(this::buildView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncOrders(
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end) {
        Map<String, Object> result = new HashMap<>();
        if (!syncing.compareAndSet(false, true)) {
            result.put("success", false);
            result.put("message", "同步进行中，请稍后再试");
            return ResponseEntity.status(429).body(result);
        }

        try {
            PlatformAdapter adapter = platformAdapterFactory.getAdapter("ozon");
            if (!(adapter instanceof OzonAdapter)) {
                result.put("success", false);
                result.put("message", "Adapter is not OzonAdapter");
                syncing.set(false);
                return ResponseEntity.internalServerError().body(result);
            }
            OzonAdapter ozonAdapter = (OzonAdapter) adapter;
            CompletableFuture.runAsync(() -> {
                try {
                    performSync(ozonAdapter, start, end);
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

    private void performSync(OzonAdapter adapter, String start, String end) {
        OzonPostingSyncResult syncResult = adapter.fetchPostings(start, end);
        if (syncResult == null || syncResult.getPostings() == null || syncResult.getPostings().isEmpty()) {
            return;
        }
        ozonPostingItemService.deleteAll();
        ozonPostingService.deleteAll();
        ozonPostingService.saveBatch(syncResult.getPostings());
        if (syncResult.getItems() != null && !syncResult.getItems().isEmpty()) {
            ozonPostingItemService.saveBatch(syncResult.getItems());
        }
    }

    private OzonPostingView buildView(OzonPosting posting) {
        OzonPostingView view = new OzonPostingView();
        view.setPostingNumber(posting.getPostingNumber());
        view.setOrderId(posting.getOrderId());
        view.setOrderNumber(posting.getOrderNumber());
        view.setStatus(posting.getStatus());
        view.setSubstatus(posting.getSubstatus());
        view.setTrackingNumber(posting.getTrackingNumber());
        view.setDeliveryMethodName(posting.getDeliveryMethodName());
        view.setWarehouseName(posting.getWarehouseName());
        view.setTplProviderName(posting.getTplProviderName());
        view.setInProcessAt(posting.getInProcessAt());
        view.setShipmentDate(posting.getShipmentDate());
        view.setShipmentDateWithoutDelay(posting.getShipmentDateWithoutDelay());
        view.setDeliveringDate(posting.getDeliveringDate());
        view.setLastChangedAt(posting.getLastChangedAt());
        view.setAvailableActions(posting.getAvailableActions());
        return view;
    }

    public static class OzonPostingView {
        private String postingNumber;
        private Long orderId;
        private String orderNumber;
        private String status;
        private String substatus;
        private String trackingNumber;
        private String deliveryMethodName;
        private String warehouseName;
        private String tplProviderName;
        private LocalDateTime inProcessAt;
        private LocalDateTime shipmentDate;
        private LocalDateTime shipmentDateWithoutDelay;
        private LocalDateTime deliveringDate;
        private LocalDateTime lastChangedAt;
        private String availableActions;

        public String getPostingNumber() {
            return postingNumber;
        }

        public void setPostingNumber(String postingNumber) {
            this.postingNumber = postingNumber;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubstatus() {
            return substatus;
        }

        public void setSubstatus(String substatus) {
            this.substatus = substatus;
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public String getDeliveryMethodName() {
            return deliveryMethodName;
        }

        public void setDeliveryMethodName(String deliveryMethodName) {
            this.deliveryMethodName = deliveryMethodName;
        }

        public String getWarehouseName() {
            return warehouseName;
        }

        public void setWarehouseName(String warehouseName) {
            this.warehouseName = warehouseName;
        }

        public String getTplProviderName() {
            return tplProviderName;
        }

        public void setTplProviderName(String tplProviderName) {
            this.tplProviderName = tplProviderName;
        }

        public LocalDateTime getInProcessAt() {
            return inProcessAt;
        }

        public void setInProcessAt(LocalDateTime inProcessAt) {
            this.inProcessAt = inProcessAt;
        }

        public LocalDateTime getShipmentDate() {
            return shipmentDate;
        }

        public void setShipmentDate(LocalDateTime shipmentDate) {
            this.shipmentDate = shipmentDate;
        }

        public LocalDateTime getShipmentDateWithoutDelay() {
            return shipmentDateWithoutDelay;
        }

        public void setShipmentDateWithoutDelay(LocalDateTime shipmentDateWithoutDelay) {
            this.shipmentDateWithoutDelay = shipmentDateWithoutDelay;
        }

        public LocalDateTime getDeliveringDate() {
            return deliveringDate;
        }

        public void setDeliveringDate(LocalDateTime deliveringDate) {
            this.deliveringDate = deliveringDate;
        }

        public LocalDateTime getLastChangedAt() {
            return lastChangedAt;
        }

        public void setLastChangedAt(LocalDateTime lastChangedAt) {
            this.lastChangedAt = lastChangedAt;
        }

        public String getAvailableActions() {
            return availableActions;
        }

        public void setAvailableActions(String availableActions) {
            this.availableActions = availableActions;
        }
    }
}
