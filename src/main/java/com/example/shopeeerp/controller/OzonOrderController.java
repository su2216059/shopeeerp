package com.example.shopeeerp.controller;

import com.example.shopeeerp.adapter.PlatformAdapter;
import com.example.shopeeerp.adapter.PlatformAdapterFactory;
import com.example.shopeeerp.adapter.dto.ozon.OzonPostingSyncResult;
import com.example.shopeeerp.adapter.impl.OzonAdapter;
import com.example.shopeeerp.pojo.OzonPosting;
import com.example.shopeeerp.pojo.OzonPostingItem;
import com.example.shopeeerp.pojo.OzonProfitOperation;
import com.example.shopeeerp.pojo.OzonProduct;
import com.example.shopeeerp.pojo.OzonProductImage;
import com.example.shopeeerp.service.OzonPostingItemService;
import com.example.shopeeerp.service.OzonPostingService;
import com.example.shopeeerp.service.OzonProfitOperationService;
import com.example.shopeeerp.service.OzonProductImageService;
import com.example.shopeeerp.service.OzonProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final OzonProfitOperationService ozonProfitOperationService;
    private final OzonProductService ozonProductService;
    private final OzonProductImageService ozonProductImageService;
    private final PlatformAdapterFactory platformAdapterFactory;
    private final AtomicBoolean syncing = new AtomicBoolean(false);
    private final Executor syncExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    public OzonOrderController(OzonPostingService ozonPostingService,
                               OzonPostingItemService ozonPostingItemService,
                               OzonProfitOperationService ozonProfitOperationService,
                               OzonProductService ozonProductService,
                               OzonProductImageService ozonProductImageService,
                               PlatformAdapterFactory platformAdapterFactory) {
        this.ozonPostingService = ozonPostingService;
        this.ozonPostingItemService = ozonPostingItemService;
        this.ozonProfitOperationService = ozonProfitOperationService;
        this.ozonProductService = ozonProductService;
        this.ozonProductImageService = ozonProductImageService;
        this.platformAdapterFactory = platformAdapterFactory;
    }

    @GetMapping
    public ResponseEntity<List<OzonPostingView>> list(
            @RequestParam(value = "created_from", required = false) String createdFrom,
            @RequestParam(value = "created_to", required = false) String createdTo,
            @RequestParam(value = "status", required = false) String status) {
        LocalDateTime from = parseDateTime(createdFrom);
        LocalDateTime to = parseDateTime(createdTo);
        String statusFilter = status != null ? status.trim() : null;

        List<OzonPosting> postings = ozonPostingService.getAll();
        List<OzonPosting> filtered = postings.stream()
                .filter(p -> matchesCreatedTime(p, from, to))
                .filter(p -> statusFilter == null || statusFilter.isEmpty() || statusFilter.equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.toList());
        Map<String, List<OzonProfitOperation>> profitMap = loadProfitOperations(filtered);
        List<OzonPostingView> result = filtered.stream()
                .map(p -> buildView(p, profitMap.get(p.getPostingNumber())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{postingNumber}/purchase-amount")
    public ResponseEntity<Map<String, Object>> updatePurchaseAmount(
            @PathVariable("postingNumber") String postingNumber,
            @RequestBody(required = false) PurchaseAmountRequest request) {
        Map<String, Object> resp = new HashMap<>();
        if (postingNumber == null || postingNumber.trim().isEmpty()) {
            resp.put("success", false);
            resp.put("message", "posting_number is required");
            return ResponseEntity.badRequest().body(resp);
        }
        OzonPosting existing = ozonPostingService.getByPostingNumber(postingNumber);
        if (existing == null) {
            resp.put("success", false);
            resp.put("message", "posting_number not found");
            return ResponseEntity.status(404).body(resp);
        }
        BigDecimal amount = request != null ? request.getPurchaseAmount() : null;
        boolean updated = ozonPostingService.updatePurchaseAmount(postingNumber, amount);
        if (!updated) {
            resp.put("success", false);
            resp.put("message", "update failed");
            return ResponseEntity.internalServerError().body(resp);
        }
        resp.put("success", true);
        resp.put("message", "ok");
        return ResponseEntity.ok(resp);
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
        Map<String, List<OzonPostingItem>> itemsByPosting = new HashMap<>();
        if (syncResult.getItems() != null && !syncResult.getItems().isEmpty()) {
            itemsByPosting = syncResult.getItems().stream()
                    .filter(item -> item.getPostingNumber() != null && !item.getPostingNumber().trim().isEmpty())
                    .collect(Collectors.groupingBy(OzonPostingItem::getPostingNumber));
        }

        for (OzonPosting posting : syncResult.getPostings()) {
            if (posting == null || posting.getPostingNumber() == null || posting.getPostingNumber().trim().isEmpty()) {
                continue;
            }
            OzonPosting existing = ozonPostingService.getByPostingNumber(posting.getPostingNumber());
            if (existing == null) {
                ozonPostingService.save(posting);
            } else {
                posting.setCreatedAt(existing.getCreatedAt());
                posting.setPurchaseAmount(existing.getPurchaseAmount());
                ozonPostingService.update(posting);
            }

            List<OzonPostingItem> items = itemsByPosting.get(posting.getPostingNumber());
            if (items != null && !items.isEmpty()) {
                ozonPostingItemService.deleteByPostingNumber(posting.getPostingNumber());
                ozonPostingItemService.saveBatch(items);
            }
        }
    }

    private OzonPostingView buildView(OzonPosting posting, List<OzonProfitOperation> profitOperations) {
        OzonPostingView view = new OzonPostingView();
        List<OzonPostingItem> items = ozonPostingItemService.getByPostingNumber(posting.getPostingNumber());
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
        view.setPrice(sumItemsPrice(items));
        view.setImageUrl(resolveImageUrl(items));
        BigDecimal ozonSalesAmount = sumAccrualsForSale(profitOperations);
        BigDecimal ozonLogisticsFee = sumLogisticsFee(profitOperations);
        BigDecimal ozonCommission = sumSaleCommission(profitOperations);
        BigDecimal ozonServiceFee = sumServiceFee(profitOperations);
        BigDecimal ozonRefund = sumRefund(profitOperations);
        BigDecimal ozonTaxFee = sumTaxFee(profitOperations);
        view.setOzonSalesAmount(ozonSalesAmount);
        view.setOzonLogisticsFee(ozonLogisticsFee);
        view.setOzonCommission(ozonCommission);
        view.setOzonServiceFee(ozonServiceFee);
        view.setOzonRefund(ozonRefund);
        view.setOzonTaxFee(ozonTaxFee);
        BigDecimal purchaseAmount = posting.getPurchaseAmount() != null ? posting.getPurchaseAmount() : BigDecimal.ZERO;
        view.setPurchaseAmount(purchaseAmount);
        BigDecimal profit = defaultZero(ozonSalesAmount)
                .add(defaultZero(ozonLogisticsFee))
                .add(defaultZero(ozonCommission))
                .add(defaultZero(ozonServiceFee))
                .add(defaultZero(ozonRefund))
                .subtract(purchaseAmount);
        view.setProfit(profit);
        return view;
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private Map<String, List<OzonProfitOperation>> loadProfitOperations(List<OzonPosting> postings) {
        if (postings == null || postings.isEmpty()) {
            return new HashMap<>();
        }
        List<String> postingNumbers = postings.stream()
                .map(OzonPosting::getPostingNumber)
                .filter(pn -> pn != null && !pn.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
        if (postingNumbers.isEmpty()) {
            return new HashMap<>();
        }
        List<OzonProfitOperation> operations = ozonProfitOperationService.getByPostingNumbers(postingNumbers);
        if (operations == null || operations.isEmpty()) {
            return new HashMap<>();
        }
        return operations.stream()
                .filter(op -> op.getPostingNumber() != null && !op.getPostingNumber().trim().isEmpty())
                .collect(Collectors.groupingBy(OzonProfitOperation::getPostingNumber));
    }

    private BigDecimal sumAccrualsForSale(List<OzonProfitOperation> ops) {
        return sumBigDecimal(ops, OzonProfitOperation::getAccrualsForSale);
    }

    private BigDecimal sumSaleCommission(List<OzonProfitOperation> ops) {
        return sumBigDecimal(ops, OzonProfitOperation::getSaleCommission);
    }

    private BigDecimal sumLogisticsFee(List<OzonProfitOperation> ops) {
        if (ops == null || ops.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (OzonProfitOperation op : ops) {
            if (op == null) {
                continue;
            }
            if (op.getDeliveryCharge() != null) {
                total = total.add(op.getDeliveryCharge());
            }
            if (op.getReturnDeliveryCharge() != null) {
                total = total.add(op.getReturnDeliveryCharge());
            }
        }
        return total;
    }

    private BigDecimal sumServiceFee(List<OzonProfitOperation> ops) {
        if (ops == null || ops.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (OzonProfitOperation op : ops) {
            if (op == null || op.getAmount() == null) {
                continue;
            }
            if (isServiceFee(op)) {
                total = total.add(op.getAmount());
            }
        }
        return total;
    }

    private BigDecimal sumRefund(List<OzonProfitOperation> ops) {
        if (ops == null || ops.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (OzonProfitOperation op : ops) {
            if (op == null || op.getAmount() == null) {
                continue;
            }
            if (isRefund(op)) {
                total = total.add(op.getAmount());
            }
        }
        return total;
    }

    private BigDecimal sumTaxFee(List<OzonProfitOperation> ops) {
        if (ops == null || ops.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (OzonProfitOperation op : ops) {
            if (op == null || op.getAmount() == null) {
                continue;
            }
            if (isTax(op)) {
                total = total.add(op.getAmount());
            }
        }
        return total;
    }

    private BigDecimal sumBigDecimal(List<OzonProfitOperation> ops, java.util.function.Function<OzonProfitOperation, BigDecimal> extractor) {
        if (ops == null || ops.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (OzonProfitOperation op : ops) {
            if (op == null) {
                continue;
            }
            BigDecimal value = extractor.apply(op);
            if (value != null) {
                total = total.add(value);
            }
        }
        return total;
    }

    private boolean isServiceFee(OzonProfitOperation op) {
        return matchToken(op.getType(), "services", "service")
                || matchToken(op.getOperationType(), "service")
                || matchToken(op.getOperationTypeName(), "服务", "услуг");
    }

    private boolean isRefund(OzonProfitOperation op) {
        return matchToken(op.getType(), "return", "returns", "refund")
                || matchToken(op.getOperationType(), "return", "refund")
                || matchToken(op.getOperationTypeName(), "退", "возврат");
    }

    private boolean isTax(OzonProfitOperation op) {
        return matchToken(op.getType(), "tax", "vat", "nds")
                || matchToken(op.getOperationType(), "tax", "vat", "nds")
                || matchToken(op.getOperationTypeName(), "税", "ндс", "налог");
    }

    private boolean matchToken(String value, String... tokens) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        String lower = value.toLowerCase();
        for (String token : tokens) {
            if (token != null && !token.isEmpty() && lower.contains(token.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private BigDecimal sumItemsPrice(List<OzonPostingItem> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (OzonPostingItem item : items) {
            if (item == null || item.getPrice() == null) {
                continue;
            }
            int qty = item.getQuantity() != null ? item.getQuantity() : 1;
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(qty)));
        }
        return total.compareTo(BigDecimal.ZERO) == 0 ? null : total;
    }

    private String resolveImageUrl(List<OzonPostingItem> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        for (OzonPostingItem item : items) {
            if (item == null) {
                continue;
            }
            OzonProduct product = null;
            if (item.getSku() != null) {
                product = ozonProductService.getBySku(item.getSku());
            }
            if (product == null && item.getOfferId() != null && !item.getOfferId().trim().isEmpty()) {
                product = ozonProductService.getByOfferId(item.getOfferId().trim());
            }
            if (product == null || product.getId() == null) {
                continue;
            }
            List<OzonProductImage> images = ozonProductImageService.getByProductId(product.getId());
            if (images == null || images.isEmpty()) {
                continue;
            }
            return images.stream()
                    .sorted(Comparator
                            .comparing(OzonProductImage::getIsPrimary, Comparator.nullsLast(Comparator.reverseOrder()))
                            .thenComparing(OzonProductImage::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                            .thenComparing(OzonProductImage::getId))
                    .map(OzonProductImage::getImageUrl)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value.trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(value.trim(), DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean matchesCreatedTime(OzonPosting posting, LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) {
            return true;
        }
        LocalDateTime createdAt = resolveOrderCreatedAt(posting);
        if (createdAt == null) {
            return false;
        }
        if (from != null && createdAt.isBefore(from)) {
            return false;
        }
        if (to != null && createdAt.isAfter(to)) {
            return false;
        }
        return true;
    }

    private LocalDateTime resolveOrderCreatedAt(OzonPosting posting) {
        if (posting == null) {
            return null;
        }
        if (posting.getInProcessAt() != null) {
            return posting.getInProcessAt();
        }
        return posting.getCreatedAt();
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
        private String imageUrl;
        private BigDecimal price;
        private BigDecimal ozonTaxFee;
        private BigDecimal ozonSalesAmount;
        private BigDecimal ozonLogisticsFee;
        private BigDecimal ozonCommission;
        private BigDecimal ozonServiceFee;
        private BigDecimal ozonRefund;
        private BigDecimal purchaseAmount;
        private BigDecimal profit;

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

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getOzonTaxFee() {
            return ozonTaxFee;
        }

        public void setOzonTaxFee(BigDecimal ozonTaxFee) {
            this.ozonTaxFee = ozonTaxFee;
        }

        public BigDecimal getOzonSalesAmount() {
            return ozonSalesAmount;
        }

        public void setOzonSalesAmount(BigDecimal ozonSalesAmount) {
            this.ozonSalesAmount = ozonSalesAmount;
        }

        public BigDecimal getOzonLogisticsFee() {
            return ozonLogisticsFee;
        }

        public void setOzonLogisticsFee(BigDecimal ozonLogisticsFee) {
            this.ozonLogisticsFee = ozonLogisticsFee;
        }

        public BigDecimal getOzonCommission() {
            return ozonCommission;
        }

        public void setOzonCommission(BigDecimal ozonCommission) {
            this.ozonCommission = ozonCommission;
        }

        public BigDecimal getOzonServiceFee() {
            return ozonServiceFee;
        }

        public void setOzonServiceFee(BigDecimal ozonServiceFee) {
            this.ozonServiceFee = ozonServiceFee;
        }

        public BigDecimal getOzonRefund() {
            return ozonRefund;
        }

        public void setOzonRefund(BigDecimal ozonRefund) {
            this.ozonRefund = ozonRefund;
        }

        public BigDecimal getPurchaseAmount() {
            return purchaseAmount;
        }

        public void setPurchaseAmount(BigDecimal purchaseAmount) {
            this.purchaseAmount = purchaseAmount;
        }

        public BigDecimal getProfit() {
            return profit;
        }

        public void setProfit(BigDecimal profit) {
            this.profit = profit;
        }
    }

    public static class PurchaseAmountRequest {
        private BigDecimal purchaseAmount;

        public BigDecimal getPurchaseAmount() {
            return purchaseAmount;
        }

        public void setPurchaseAmount(BigDecimal purchaseAmount) {
            this.purchaseAmount = purchaseAmount;
        }
    }
}
