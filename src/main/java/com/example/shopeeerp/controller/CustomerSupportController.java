package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.CustomerSupport;
import com.example.shopeeerp.service.CustomerSupportService;
import com.example.shopeeerp.security.ShopPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户支持控制器
 */
@RestController
@RequestMapping("/customer-support")
@CrossOrigin(origins = "*")
public class CustomerSupportController {

    @Autowired
    private CustomerSupportService customerSupportService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_SUPPORT_VIEW')")
    @ShopPermission
    public ResponseEntity<List<CustomerSupport>> getAllCustomerSupports(@RequestParam("shopId") Long shopId) {
        List<CustomerSupport> supports = customerSupportService.selectAll(shopId);
        return ResponseEntity.ok(supports);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_SUPPORT_VIEW')")
    @ShopPermission
    public ResponseEntity<CustomerSupport> getCustomerSupportById(@PathVariable Long id,
                                                                  @RequestParam("shopId") Long shopId) {
        CustomerSupport support = customerSupportService.selectById(id, shopId);
        if (support != null) {
            return ResponseEntity.ok(support);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER_SUPPORT_CREATE')")
    @ShopPermission
    public ResponseEntity<CustomerSupport> createCustomerSupport(@RequestBody CustomerSupport customerSupport,
                                                                 @RequestParam("shopId") Long shopId) {
        LocalDateTime now = LocalDateTime.now();
        customerSupport.setShopId(shopId);
        customerSupport.setCreatedAt(now);
        customerSupport.setUpdatedAt(now);
        int result = customerSupportService.insert(customerSupport);
        if (result > 0) {
            return ResponseEntity.ok(customerSupport);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_SUPPORT_UPDATE')")
    @ShopPermission
    public ResponseEntity<CustomerSupport> updateCustomerSupport(@PathVariable Long id,
                                                                 @RequestBody CustomerSupport customerSupport,
                                                                 @RequestParam("shopId") Long shopId) {
        customerSupport.setSupportId(id);
        customerSupport.setShopId(shopId);
        customerSupport.setUpdatedAt(LocalDateTime.now());
        int result = customerSupportService.update(customerSupport);
        if (result > 0) {
            return ResponseEntity.ok(customerSupport);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_SUPPORT_DELETE')")
    @ShopPermission
    public ResponseEntity<Void> deleteCustomerSupport(@PathVariable Long id,
                                                      @RequestParam("shopId") Long shopId) {
        int result = customerSupportService.deleteById(id, shopId);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('CUSTOMER_SUPPORT_VIEW')")
    @ShopPermission
    public ResponseEntity<List<CustomerSupport>> getCustomerSupportsByCustomerId(@PathVariable Long customerId,
                                                                                 @RequestParam("shopId") Long shopId) {
        List<CustomerSupport> supports = customerSupportService.selectByCustomerId(customerId, shopId);
        return ResponseEntity.ok(supports);
    }
}
