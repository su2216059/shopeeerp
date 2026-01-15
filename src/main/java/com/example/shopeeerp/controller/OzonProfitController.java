package com.example.shopeeerp.controller;

import com.example.shopeeerp.service.OzonProfitSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ozon/profit")
@CrossOrigin(origins = "*")
public class OzonProfitController {

    @Autowired
    private OzonProfitSyncService profitSyncService;

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> sync(
            @RequestParam(value = "posting_number", required = false) String postingNumber,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if ((postingNumber == null || postingNumber.trim().isEmpty())
                    && (from == null || from.trim().isEmpty())
                    && (to == null || to.trim().isEmpty())) {
                resp.put("success", false);
                resp.put("message", "posting_number 和 日期（from/to）不能同时为空");
                return ResponseEntity.badRequest().body(resp);
            }

            profitSyncService.sync(postingNumber, from, to);
            resp.put("success", true);
            resp.put("message", "同步完成（采购成本默认0）");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }
}
