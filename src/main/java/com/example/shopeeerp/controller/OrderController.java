package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.Order;
import com.example.shopeeerp.service.OrderService;
import com.example.shopeeerp.security.ShopPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAuthority('ORDER_VIEW')")
    @ShopPermission
    public ResponseEntity<List<Order>> getAllOrders(@RequestParam("shopId") Long shopId) {
        List<Order> orders = orderService.selectAll(shopId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_VIEW')")
    @ShopPermission
    public ResponseEntity<Order> getOrderById(@PathVariable Long id,
                                              @RequestParam("shopId") Long shopId) {
        Order order = orderService.selectById(id, shopId);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ORDER_CREATE')")
    @ShopPermission
    public ResponseEntity<Order> createOrder(@RequestBody Order order,
                                             @RequestParam("shopId") Long shopId) {
        LocalDateTime now = LocalDateTime.now();
        order.setShopId(shopId);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        int result = orderService.insert(order);
        if (result > 0) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_UPDATE')")
    @ShopPermission
    public ResponseEntity<Order> updateOrder(@PathVariable Long id,
                                             @RequestBody Order order,
                                             @RequestParam("shopId") Long shopId) {
        order.setOrderId(id);
        order.setShopId(shopId);
        order.setUpdatedAt(LocalDateTime.now());
        int result = orderService.update(order);
        if (result > 0) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_DELETE')")
    @ShopPermission
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id,
                                            @RequestParam("shopId") Long shopId) {
        int result = orderService.deleteById(id, shopId);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('ORDER_VIEW')")
    @ShopPermission
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable Long customerId,
                                                             @RequestParam("shopId") Long shopId) {
        List<Order> orders = orderService.selectByCustomerId(customerId, shopId);
        return ResponseEntity.ok(orders);
    }
}
