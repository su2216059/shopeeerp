package com.example.shopeeerp.controller;

import com.example.shopeeerp.service.OzonCashflowSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ozon/cashflow")
@CrossOrigin(origins = "*")
public class OzonCashflowController {

    @Autowired
    private OzonCashflowSyncService cashflowSyncService;

    @GetMapping("/sync")
    public ResponseEntity<Map<String, Object>> sync(
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end) {
        Map<String, Object> resp = new HashMap<>();
        try {
            LocalDateTime from = parseIso(start);
            LocalDateTime to = parseIso(end);
            cashflowSyncService.sync(from, to);
            resp.put("success", true);
            resp.put("message", "财务报表同步完成");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    private LocalDateTime parseIso(String v) {
        if (v == null || v.trim().isEmpty()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(v.trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }
}
