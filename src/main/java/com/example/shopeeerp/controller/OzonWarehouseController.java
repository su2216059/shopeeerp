package com.example.shopeeerp.controller;

import com.example.shopeeerp.adapter.PlatformAdapter;
import com.example.shopeeerp.adapter.PlatformAdapterFactory;
import com.example.shopeeerp.adapter.dto.ozon.OzonDeliveryMethodListResponse;
import com.example.shopeeerp.adapter.dto.ozon.OzonWarehouseListResponse;
import com.example.shopeeerp.adapter.impl.OzonAdapter;
import com.example.shopeeerp.pojo.OzonDeliveryMethod;
import com.example.shopeeerp.pojo.OzonWarehouse;
import com.example.shopeeerp.service.OzonDeliveryMethodService;
import com.example.shopeeerp.service.OzonWarehouseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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
@RequestMapping("/ozon/warehouses")
@CrossOrigin(origins = "*")
public class OzonWarehouseController {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final OzonWarehouseService ozonWarehouseService;
    private final OzonDeliveryMethodService ozonDeliveryMethodService;
    private final PlatformAdapterFactory platformAdapterFactory;
    private final AtomicBoolean syncing = new AtomicBoolean(false);
    private final Executor syncExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    public OzonWarehouseController(OzonWarehouseService ozonWarehouseService,
                                   OzonDeliveryMethodService ozonDeliveryMethodService,
                                   PlatformAdapterFactory platformAdapterFactory) {
        this.ozonWarehouseService = ozonWarehouseService;
        this.ozonDeliveryMethodService = ozonDeliveryMethodService;
        this.platformAdapterFactory = platformAdapterFactory;
    }

    @GetMapping
    public ResponseEntity<List<OzonWarehouseView>> list() {
        List<OzonWarehouse> warehouses = ozonWarehouseService.getAll();
        List<Long> warehouseIds = warehouses.stream()
                .map(OzonWarehouse::getWarehouseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, List<OzonDeliveryMethod>> methodsByWarehouse = buildMethodsMap(warehouseIds);

        List<OzonWarehouseView> result = warehouses.stream()
                .map(w -> buildView(w, methodsByWarehouse.get(w.getWarehouseId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> sync() {
        Map<String, Object> result = new HashMap<>();
        if (!syncing.compareAndSet(false, true)) {
            result.put("success", false);
            result.put("message", "sync in progress");
            return ResponseEntity.status(429).body(result);
        }

        try {
            PlatformAdapter adapter = platformAdapterFactory.getAdapter("ozon");
            if (!(adapter instanceof OzonAdapter)) {
                syncing.set(false);
                result.put("success", false);
                result.put("message", "Adapter is not OzonAdapter");
                return ResponseEntity.internalServerError().body(result);
            }
            OzonAdapter ozonAdapter = (OzonAdapter) adapter;
            CompletableFuture.runAsync(() -> {
                try {
                    performSync(ozonAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    syncing.set(false);
                }
            }, syncExecutor);

            result.put("success", true);
            result.put("message", "sync started");
            return ResponseEntity.accepted().body(result);
        } catch (Exception e) {
            syncing.set(false);
            result.put("success", false);
            result.put("message", "Sync failed to start");
            return ResponseEntity.internalServerError().body(result);
        }
    }

    private void performSync(OzonAdapter ozonAdapter) {
        List<OzonWarehouseListResponse.Warehouse> warehouseResponses = ozonAdapter.fetchWarehouses(100);
        if (warehouseResponses == null || warehouseResponses.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (OzonWarehouseListResponse.Warehouse warehouseResp : warehouseResponses) {
            if (warehouseResp == null || warehouseResp.getWarehouse_id() == null) {
                continue;
            }

            OzonWarehouse entity = mapWarehouse(warehouseResp, now);
            OzonWarehouse existing = ozonWarehouseService.getByWarehouseId(entity.getWarehouseId());
            if (existing != null) {
                entity.setCreatedAt(existing.getCreatedAt());
                ozonWarehouseService.update(entity);
            } else {
                entity.setCreatedAt(now);
                ozonWarehouseService.save(entity);
            }

            List<OzonDeliveryMethodListResponse.DeliveryMethod> methods =
                    ozonAdapter.fetchDeliveryMethods(entity.getWarehouseId(), 100);
            if (methods == null) {
                continue;
            }

            ozonDeliveryMethodService.removeByWarehouseId(entity.getWarehouseId());
            if (!methods.isEmpty()) {
                List<OzonDeliveryMethod> toSave = new ArrayList<>();
                for (OzonDeliveryMethodListResponse.DeliveryMethod methodResp : methods) {
                    OzonDeliveryMethod method = mapDeliveryMethod(methodResp, now);
                    if (method != null) {
                        toSave.add(method);
                    }
                }
                if (!toSave.isEmpty()) {
                    ozonDeliveryMethodService.saveBatch(toSave);
                }
            }
        }
    }

    private OzonWarehouse mapWarehouse(OzonWarehouseListResponse.Warehouse warehouseResp, LocalDateTime now) {
        OzonWarehouse entity = new OzonWarehouse();
        entity.setWarehouseId(warehouseResp.getWarehouse_id());
        entity.setName(warehouseResp.getName());
        entity.setStoreName("Ozon");
        entity.setStatus(warehouseResp.getStatus());
        entity.setWorkingDays(joinWorkingDays(warehouseResp.getWorking_days()));
        entity.setMinWorkingDays(warehouseResp.getMin_working_days());
        entity.setPostingsLimit(warehouseResp.getPostings_limit());
        entity.setMinPostingsLimit(warehouseResp.getMin_postings_limit());
        entity.setIsRfbs(warehouseResp.getIs_rfbs());
        entity.setHasEntrustedAcceptance(warehouseResp.getHas_entrusted_acceptance());
        entity.setCanPrintActInAdvance(warehouseResp.getCan_print_act_in_advance());
        entity.setHasPostingsLimit(warehouseResp.getHas_postings_limit());
        entity.setIsKarantin(warehouseResp.getIs_karantin());
        entity.setIsKgt(warehouseResp.getIs_kgt());
        entity.setIsTimetableEditable(warehouseResp.getIs_timetable_editable());
        entity.setFirstMileTypeJson(toJson(warehouseResp.getFirst_mile_type()));
        entity.setUpdatedAt(now);
        return entity;
    }

    private OzonDeliveryMethod mapDeliveryMethod(OzonDeliveryMethodListResponse.DeliveryMethod methodResp, LocalDateTime now) {
        if (methodResp == null || methodResp.getId() == null) {
            return null;
        }
        OzonDeliveryMethod method = new OzonDeliveryMethod();
        method.setId(methodResp.getId());
        method.setWarehouseId(methodResp.getWarehouse_id());
        method.setName(methodResp.getName());
        method.setStatus(methodResp.getStatus());
        method.setCutoff(methodResp.getCutoff());
        method.setProviderId(methodResp.getProvider_id());
        method.setCompanyId(methodResp.getCompany_id());
        method.setTemplateId(methodResp.getTemplate_id());
        method.setSlaCutIn(methodResp.getSla_cut_in());
        LocalDateTime createdAt = parseDateTime(methodResp.getCreated_at());
        LocalDateTime updatedAt = parseDateTime(methodResp.getUpdated_at());
        method.setCreatedAt(createdAt != null ? createdAt : now);
        method.setUpdatedAt(updatedAt != null ? updatedAt : now);
        return method;
    }

    private String joinWorkingDays(List<String> days) {
        if (days == null || days.isEmpty()) {
            return null;
        }
        return String.join(",", days);
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value.trim()).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    private OzonWarehouseView buildView(OzonWarehouse warehouse, List<OzonDeliveryMethod> methods) {
        OzonWarehouseView view = new OzonWarehouseView();
        view.setWarehouseId(warehouse.getWarehouseId());
        view.setName(warehouse.getName());
        view.setStoreName(warehouse.getStoreName());
        view.setWorkingDays(warehouse.getWorkingDays());
        view.setStatus(warehouse.getStatus());
        view.setMinWorkingDays(warehouse.getMinWorkingDays());
        view.setPostingsLimit(warehouse.getPostingsLimit());
        view.setMinPostingsLimit(warehouse.getMinPostingsLimit());
        view.setIsRfbs(warehouse.getIsRfbs());
        view.setCreatedAt(warehouse.getCreatedAt());
        if (methods != null && !methods.isEmpty()) {
            List<DeliveryMethodView> list = methods.stream()
                    .map(this::buildDeliveryMethodView)
                    .collect(Collectors.toList());
            view.setDeliveryMethods(list);
        } else {
            view.setDeliveryMethods(new ArrayList<>());
        }
        return view;
    }

    private DeliveryMethodView buildDeliveryMethodView(OzonDeliveryMethod method) {
        DeliveryMethodView view = new DeliveryMethodView();
        view.setId(method.getId());
        view.setName(method.getName());
        view.setStatus(method.getStatus());
        return view;
    }

    private Map<Long, List<OzonDeliveryMethod>> buildMethodsMap(List<Long> warehouseIds) {
        if (warehouseIds == null || warehouseIds.isEmpty()) {
            return new HashMap<>();
        }
        List<OzonDeliveryMethod> methods = ozonDeliveryMethodService.getByWarehouseIds(warehouseIds);
        if (methods == null || methods.isEmpty()) {
            return new HashMap<>();
        }
        return methods.stream()
                .filter(m -> m.getWarehouseId() != null)
                .collect(Collectors.groupingBy(OzonDeliveryMethod::getWarehouseId));
    }

    public static class OzonWarehouseView {
        private Long warehouseId;
        private String name;
        private String storeName;
        private String workingDays;
        private String status;
        private Integer minWorkingDays;
        private Integer postingsLimit;
        private Integer minPostingsLimit;
        private Boolean isRfbs;
        private LocalDateTime createdAt;
        private List<DeliveryMethodView> deliveryMethods;

        public Long getWarehouseId() {
            return warehouseId;
        }

        public void setWarehouseId(Long warehouseId) {
            this.warehouseId = warehouseId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStoreName() {
            return storeName;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }

        public String getWorkingDays() {
            return workingDays;
        }

        public void setWorkingDays(String workingDays) {
            this.workingDays = workingDays;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getMinWorkingDays() {
            return minWorkingDays;
        }

        public void setMinWorkingDays(Integer minWorkingDays) {
            this.minWorkingDays = minWorkingDays;
        }

        public Integer getPostingsLimit() {
            return postingsLimit;
        }

        public void setPostingsLimit(Integer postingsLimit) {
            this.postingsLimit = postingsLimit;
        }

        public Integer getMinPostingsLimit() {
            return minPostingsLimit;
        }

        public void setMinPostingsLimit(Integer minPostingsLimit) {
            this.minPostingsLimit = minPostingsLimit;
        }

        public Boolean getIsRfbs() {
            return isRfbs;
        }

        public void setIsRfbs(Boolean isRfbs) {
            this.isRfbs = isRfbs;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public List<DeliveryMethodView> getDeliveryMethods() {
            return deliveryMethods;
        }

        public void setDeliveryMethods(List<DeliveryMethodView> deliveryMethods) {
            this.deliveryMethods = deliveryMethods;
        }
    }

    public static class DeliveryMethodView {
        private Long id;
        private String name;
        private String status;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
