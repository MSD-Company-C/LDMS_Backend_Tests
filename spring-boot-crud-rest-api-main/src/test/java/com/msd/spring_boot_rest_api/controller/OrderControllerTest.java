package com.msd.spring_boot_rest_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msd.spring_boot_rest_api.controller.OrderController;
import com.msd.spring_boot_rest_api.model.Order;
import com.msd.spring_boot_rest_api.service.OrderService;
import com.msd.spring_boot_rest_api.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = OrderController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllOrders() throws Exception {
        // Given
        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus("PENDING");

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus("SHIPPED");

        List<Order> orders = Arrays.asList(order1, order2);
        when(orderService.getAllOrders()).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()));

        verify(orderService).getAllOrders();
    }

    @Test
    void testGetAllOrdersWithWarehouseRole() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(new Order());
        when(orderService.getAllOrders()).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk());

        verify(orderService).getAllOrders();
    }

    @Test
    void testGetAllOrdersWithDriverRole() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(new Order());
        when(orderService.getAllOrders()).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk());

        verify(orderService).getAllOrders();
    }

    @Test
    void testGetOrderById() throws Exception {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus("PENDING");

        when(orderService.getOrderById(1L)).thenReturn(Optional.of(order));

        // When & Then
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService).getOrderById(1L);
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {
        // Given
        when(orderService.getOrderById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());

        verify(orderService).getOrderById(999L);
    }


    @Test
    void testUpdateOrderStatus() throws Exception {
        // Given
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setStatus("PENDING");

        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setStatus("SHIPPED");

        OrderController.StatusUpdateRequest request = new OrderController.StatusUpdateRequest();
        request.setStatus("SHIPPED");

        // Mock the service to return the existing order when fetching by ID
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(existingOrder));
        // Mock the service to return the updated order when updating status
        when(orderService.updateOrderStatus(1L, "SHIPPED")).thenReturn(updatedOrder);

        // When & Then
        mockMvc.perform(put("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        verify(orderService).updateOrderStatus(1L, "SHIPPED");
    }

    @Test
    void testUpdateOrderStatusWithWarehouseRole() throws Exception {
        // Given
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setStatus("PROCESSING");

        OrderController.StatusUpdateRequest request = new OrderController.StatusUpdateRequest();
        request.setStatus("PROCESSING");

        when(orderService.updateOrderStatus(1L, "PROCESSING")).thenReturn(updatedOrder);

        // When & Then
        mockMvc.perform(put("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        verify(orderService).updateOrderStatus(1L, "PROCESSING");
    }

    @Test
    void testUpdateOrderStatusWithoutAuthentication() throws Exception {
        // Given
        OrderController.StatusUpdateRequest request = new OrderController.StatusUpdateRequest();
        request.setStatus("Order Processing");

        // When & Then
        mockMvc.perform(put("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(orderService);
    }
}
