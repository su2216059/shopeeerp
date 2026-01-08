package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.Inventory;
import com.example.shopeeerp.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 库存控制器
 */
@RestController
@RequestMapping("/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> inventory = inventoryService.selectAll();
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        Inventory inventory = inventoryService.selectById(id);
        if (inventory != null) {
            return ResponseEntity.ok(inventory);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
        inventory.setLastUpdated(LocalDateTime.now());
        int result = inventoryService.insert(inventory);
        if (result > 0) {
            return ResponseEntity.ok(inventory);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable Long id, @RequestBody Inventory inventory) {
        inventory.setInventoryId(id);
        inventory.setLastUpdated(LocalDateTime.now());
        int result = inventoryService.update(inventory);
        if (result > 0) {
            return ResponseEntity.ok(inventory);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        int result = inventoryService.deleteById(id);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Inventory>> getInventoryByProductId(@PathVariable Long productId) {
        List<Inventory> inventory = inventoryService.selectByProductId(productId);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<Inventory>> getInventoryByWarehouseId(@PathVariable Long warehouseId) {
        List<Inventory> inventory = inventoryService.selectByWarehouseId(warehouseId);
        return ResponseEntity.ok(inventory);
    }
}
