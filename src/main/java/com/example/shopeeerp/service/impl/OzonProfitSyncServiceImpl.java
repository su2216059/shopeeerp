package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.adapter.dto.ozon.OzonTransactionListResponse;
import com.example.shopeeerp.adapter.impl.OzonAdapter;
import com.example.shopeeerp.pojo.OzonProfitOperation;
import com.example.shopeeerp.service.OzonProfitOperationService;
import com.example.shopeeerp.service.OzonProfitSyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Service
public class OzonProfitSyncServiceImpl implements OzonProfitSyncService {

    @Autowired
    private OzonAdapter ozonAdapter;
    @Autowired
    private OzonProfitOperationService operationService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DateTimeFormatter SIMPLE_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public void sync(String postingNumber, String from, String to) {
        List<OzonTransactionListResponse.Operation> ops = ozonAdapter.fetchTransactions(postingNumber, from, to, 1000);
        if (ops == null || ops.isEmpty()) {
            return;
        }
        if (!Strings.isBlank(postingNumber)) {
            operationService.deleteByPostingNumber(postingNumber);
        } else if (!Strings.isBlank(from) || !Strings.isBlank(to)) {
            operationService.deleteByDateRange(parseDateTime(from), parseDateTime(to));
        }

        List<OzonProfitOperation> list = new ArrayList<>();
        for (OzonTransactionListResponse.Operation op : ops) {
            OzonProfitOperation entity = mapToEntity(op);
            if (entity != null) {
                list.add(entity);
            }
        }
        if (!list.isEmpty()) {
            operationService.saveBatch(list);
        }
    }

    private OzonProfitOperation mapToEntity(OzonTransactionListResponse.Operation op) {
        if (op == null || op.getOperationId() == null) {
            return null;
        }
        OzonProfitOperation entity = new OzonProfitOperation();
        entity.setOperationId(op.getOperationId());
        entity.setOperationType(op.getOperationType());
        entity.setOperationTypeName(op.getOperationTypeName());
        entity.setOperationDate(parseDateTime(op.getOperationDate()));
        entity.setDeliveryCharge(op.getDeliveryCharge());
        entity.setReturnDeliveryCharge(op.getReturnDeliveryCharge());
        entity.setAccrualsForSale(op.getAccrualsForSale());
        entity.setSaleCommission(op.getSaleCommission());
        entity.setAmount(op.getAmount());
        entity.setPurchaseCost(BigDecimal.ZERO);
        entity.setType(op.getType());
        if (op.getPosting() != null) {
            entity.setDeliverySchema(op.getPosting().getDeliverySchema());
            entity.setPostingNumber(op.getPosting().getPostingNumber());
            entity.setWarehouseId(op.getPosting().getWarehouseId());
            entity.setOrderDate(parseDateTime(op.getPosting().getOrderDate()));
        }
        entity.setRawItemsJson(toJson(op.getItems()));
        entity.setRawServicesJson(toJson(op.getServices()));
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private LocalDateTime parseDateTime(String value) {
        if (Strings.isBlank(value)) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim(), SIMPLE_DT);
        } catch (Exception e) {
            return null;
        }
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
}
