package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.Customer;
import com.example.shopeeerp.service.CustomerService;
import com.example.shopeeerp.security.ShopPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户控制器
 */
@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    @ShopPermission
    public ResponseEntity<List<Customer>> getAllCustomers(@RequestParam("shopId") Long shopId) {
        List<Customer> customers = customerService.selectAll(shopId);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    @ShopPermission
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id,
                                                    @RequestParam("shopId") Long shopId) {
        Customer customer = customerService.selectById(id, shopId);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER_CREATE')")
    @ShopPermission
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer,
                                                   @RequestParam("shopId") Long shopId) {
        LocalDateTime now = LocalDateTime.now();
        customer.setShopId(shopId);
        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);
        int result = customerService.insert(customer);
        if (result > 0) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE')")
    @ShopPermission
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id,
                                                   @RequestBody Customer customer,
                                                   @RequestParam("shopId") Long shopId) {
        customer.setCustomerId(id);
        customer.setShopId(shopId);
        customer.setUpdatedAt(LocalDateTime.now());
        int result = customerService.update(customer);
        if (result > 0) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_DELETE')")
    @ShopPermission
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id,
                                               @RequestParam("shopId") Long shopId) {
        int result = customerService.deleteById(id, shopId);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    @ShopPermission
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email,
                                                       @RequestParam("shopId") Long shopId) {
        Customer customer = customerService.selectByEmail(email, shopId);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.notFound().build();
    }
}
