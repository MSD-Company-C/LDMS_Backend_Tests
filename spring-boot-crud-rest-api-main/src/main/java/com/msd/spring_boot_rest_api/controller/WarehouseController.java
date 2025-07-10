package com.msd.spring_boot_rest_api.controller;

import com.msd.spring_boot_rest_api.model.Order;
import com.msd.spring_boot_rest_api.service.OrderService;
import com.msd.spring_boot_rest_api.entity.Order;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    private final OrderService orderService;

    public WarehouseController(OrderService orderService) {
        this.orderService = orderService;
    }

    
    @PreAuthorize("hasRole('WAREHOUSE')")
    @GetMapping("/all-orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
