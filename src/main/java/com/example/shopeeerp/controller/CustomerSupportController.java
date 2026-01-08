package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.CustomerSupport;
import com.example.shopeeerp.service.CustomerSupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<CustomerSupport>> getAllCustomerSupports() {
        List<CustomerSupport> supports = customerSupportService.selectAll();
        return ResponseEntity.ok(supports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerSupport> getCustomerSupportById(@PathVariable Long id) {
        CustomerSupport support = customerSupportService.selectById(id);
        if (support != null) {
            return ResponseEntity.ok(support);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<CustomerSupport> createCustomerSupport(@RequestBody CustomerSupport customerSupport) {
        LocalDateTime now = LocalDateTime.now();
        customerSupport.setCreatedAt(now);
        customerSupport.setUpdatedAt(now);
        int result = customerSupportService.insert(customerSupport);
        if (result > 0) {
            return ResponseEntity.ok(customerSupport);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerSupport> updateCustomerSupport(@PathVariable Long id, @RequestBody CustomerSupport customerSupport) {
        customerSupport.setSupportId(id);
        customerSupport.setUpdatedAt(LocalDateTime.now());
        int result = customerSupportService.update(customerSupport);
        if (result > 0) {
            return ResponseEntity.ok(customerSupport);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerSupport(@PathVariable Long id) {
        int result = customerSupportService.deleteById(id);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerSupport>> getCustomerSupportsByCustomerId(@PathVariable Long customerId) {
        List<CustomerSupport> supports = customerSupportService.selectByCustomerId(customerId);
        return ResponseEntity.ok(supports);
    }
}
