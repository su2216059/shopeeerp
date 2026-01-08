package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.SalesData;
import com.example.shopeeerp.service.SalesDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 销售数据控制器
 */
@RestController
@RequestMapping("/sales-data")
@CrossOrigin(origins = "*")
public class SalesDataController {

    @Autowired
    private SalesDataService salesDataService;

    @GetMapping
    public ResponseEntity<List<SalesData>> getAllSalesData() {
        List<SalesData> salesData = salesDataService.selectAll();
        return ResponseEntity.ok(salesData);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesData> getSalesDataById(@PathVariable Long id) {
        SalesData salesData = salesDataService.selectById(id);
        if (salesData != null) {
            return ResponseEntity.ok(salesData);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<SalesData> createSalesData(@RequestBody SalesData salesData) {
        if (salesData.getSalesDate() == null) {
            salesData.setSalesDate(LocalDateTime.now());
        }
        int result = salesDataService.insert(salesData);
        if (result > 0) {
            return ResponseEntity.ok(salesData);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesData> updateSalesData(@PathVariable Long id, @RequestBody SalesData salesData) {
        salesData.setSalesId(id);
        int result = salesDataService.update(salesData);
        if (result > 0) {
            return ResponseEntity.ok(salesData);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesData(@PathVariable Long id) {
        int result = salesDataService.deleteById(id);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SalesData>> getSalesDataByProductId(@PathVariable Long productId) {
        List<SalesData> salesData = salesDataService.selectByProductId(productId);
        return ResponseEntity.ok(salesData);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<SalesData>> getSalesDataByOrderId(@PathVariable Long orderId) {
        List<SalesData> salesData = salesDataService.selectByOrderId(orderId);
        return ResponseEntity.ok(salesData);
    }
}
