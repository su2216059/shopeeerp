package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.Invoice;
import com.example.shopeeerp.service.InvoiceService;
import com.example.shopeeerp.security.ShopPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 发票控制器
 */
@RestController
@RequestMapping("/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasAuthority('INVOICE_VIEW')")
    @ShopPermission
    public ResponseEntity<List<Invoice>> getAllInvoices(@RequestParam("shopId") Long shopId) {
        List<Invoice> invoices = invoiceService.selectAll(shopId);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVOICE_VIEW')")
    @ShopPermission
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id,
                                                  @RequestParam("shopId") Long shopId) {
        Invoice invoice = invoiceService.selectById(id, shopId);
        if (invoice != null) {
            return ResponseEntity.ok(invoice);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVOICE_CREATE')")
    @ShopPermission
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice,
                                                 @RequestParam("shopId") Long shopId) {
        if (invoice.getIssueDate() == null) {
            invoice.setIssueDate(LocalDateTime.now());
        }
        invoice.setShopId(shopId);
        int result = invoiceService.insert(invoice);
        if (result > 0) {
            return ResponseEntity.ok(invoice);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVOICE_UPDATE')")
    @ShopPermission
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id,
                                                 @RequestBody Invoice invoice,
                                                 @RequestParam("shopId") Long shopId) {
        invoice.setInvoiceId(id);
        invoice.setShopId(shopId);
        int result = invoiceService.update(invoice);
        if (result > 0) {
            return ResponseEntity.ok(invoice);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INVOICE_DELETE')")
    @ShopPermission
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id,
                                              @RequestParam("shopId") Long shopId) {
        int result = invoiceService.deleteById(id, shopId);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAuthority('INVOICE_VIEW')")
    @ShopPermission
    public ResponseEntity<List<Invoice>> getInvoicesByOrderId(@PathVariable Long orderId,
                                                              @RequestParam("shopId") Long shopId) {

        int i=0;
        while (true){
            if(i>0){
                break;
            }
        }
        List<Invoice> invoices = invoiceService.selectByOrderId(orderId, shopId);
        return ResponseEntity.ok(invoices);
    }


}
