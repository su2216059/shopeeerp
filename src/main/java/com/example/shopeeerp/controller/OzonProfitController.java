package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.OzonPosting;
import com.example.shopeeerp.service.OzonPostingService;
import com.example.shopeeerp.service.OzonProfitSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.shopeeerp.security.ShopPermission;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ozon/profit")
@CrossOrigin(origins = "*")
public class OzonProfitController {

    @Autowired
    private OzonProfitSyncService profitSyncService;
    @Autowired
    private OzonPostingService ozonPostingService;

    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('OZON_PROFIT_SYNC')")
    @ShopPermission
    public ResponseEntity<Map<String, Object>> sync(
            @RequestParam("shopId") Long shopId,
            @RequestParam(value = "posting_number", required = false) String postingNumber,
            @RequestParam(value = "order_ids", required = false) String orderIds,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (postingNumber != null && !postingNumber.trim().isEmpty()) {
                OzonPosting posting = ozonPostingService.getByPostingNumberAndShopId(postingNumber, shopId);
                if (posting == null) {
                    resp.put("success", false);
                    resp.put("message", "posting_number not found");
                    return ResponseEntity.status(404).body(resp);
                }
            }
            if (orderIds != null && !orderIds.trim().isEmpty()) {
                List<String> postingNumbers = resolvePostingNumbers(orderIds, shopId);
                if (postingNumbers.isEmpty()) {
                    resp.put("success", false);
                    resp.put("message", "未匹配到对应的订单，无法更新财务");
                    return ResponseEntity.badRequest().body(resp);
                }
                for (String pn : postingNumbers) {
                    profitSyncService.sync(pn, null, null, shopId);
                }
                resp.put("success", true);
                resp.put("message", "同步完成（采购成本默认0）");
                return ResponseEntity.ok(resp);
            }

            if ((postingNumber == null || postingNumber.trim().isEmpty())
                    && (from == null || from.trim().isEmpty())
                    && (to == null || to.trim().isEmpty())) {
                resp.put("success", false);
                resp.put("message", "order_ids、posting_number 和 日期（from/to）不能同时为空");
                return ResponseEntity.badRequest().body(resp);
            }

            profitSyncService.sync(postingNumber, from, to, shopId);
            resp.put("success", true);
            resp.put("message", "同步完成（采购成本默认0）");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    private List<String> resolvePostingNumbers(String orderIds, Long shopId) {
        List<Long> ids = parseIds(orderIds);
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<OzonPosting> postings = ozonPostingService.getAllByShopId(shopId);
        Map<Long, String> orderToPosting = postings.stream()
                .filter(p -> p.getOrderId() != null && p.getPostingNumber() != null)
                .collect(Collectors.toMap(OzonPosting::getOrderId, OzonPosting::getPostingNumber, (a, b) -> a));
        Set<String> result = new HashSet<>();
        for (Long id : ids) {
            String postingNumber = orderToPosting.get(id);
            if (postingNumber != null && !postingNumber.trim().isEmpty()) {
                result.add(postingNumber);
            }
        }
        return new ArrayList<>(result);
    }

    private List<Long> parseIds(String orderIds) {
        List<Long> result = new ArrayList<>();
        if (orderIds == null || orderIds.trim().isEmpty()) {
            return result;
        }
        String[] parts = orderIds.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                result.add(Long.parseLong(trimmed));
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }
}
