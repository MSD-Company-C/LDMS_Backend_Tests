package com.msd.spring_boot_rest_api.controller;

import com.msd.spring_boot_rest_api.model.Order;
import com.msd.spring_boot_rest_api.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // DTO class for status update request
    public static class StatusUpdateRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @GetMapping
    // @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE') or hasRole('DRIVER')")
    public List<Order> getAllOrders() {
        return service.getAllOrders();
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE') or hasRole('DRIVER')")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return service.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE')")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        service.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        return service.getOrderById(id)
                .map(order -> ResponseEntity.ok(service.updateOrderStatus(id, request.getStatus())))
                .orElse(ResponseEntity.notFound().build());
    }



}
