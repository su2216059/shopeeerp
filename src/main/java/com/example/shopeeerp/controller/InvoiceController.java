package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.Invoice;
import com.example.shopeeerp.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.selectAll();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        Invoice invoice = invoiceService.selectById(id);
        if (invoice != null) {
            return ResponseEntity.ok(invoice);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        if (invoice.getIssueDate() == null) {
            invoice.setIssueDate(LocalDateTime.now());
        }
        int result = invoiceService.insert(invoice);
        if (result > 0) {
            return ResponseEntity.ok(invoice);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoice) {
        invoice.setInvoiceId(id);
        int result = invoiceService.update(invoice);
        if (result > 0) {
            return ResponseEntity.ok(invoice);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        int result = invoiceService.deleteById(id);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Invoice>> getInvoicesByOrderId(@PathVariable Long orderId) {
        List<Invoice> invoices = invoiceService.selectByOrderId(orderId);
        return ResponseEntity.ok(invoices);
    }
}
