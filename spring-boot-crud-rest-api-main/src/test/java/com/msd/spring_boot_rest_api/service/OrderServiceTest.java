package com.msd.spring_boot_rest_api.service;

import com.msd.spring_boot_rest_api.model.Order;
import com.msd.spring_boot_rest_api.model.Driver;
import com.msd.spring_boot_rest_api.repository.OrderRepository;
import com.msd.spring_boot_rest_api.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setStatus("PENDING");
        testOrder.setEstimatedDelivery("2025-07-15");
    }

    @Test
    void testGetAllOrders() {
        // Given
        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus("Order Placed");

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus("Order Processing");

        List<Order> expectedOrders = Arrays.asList(order1, order2);
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        // When
        List<Order> actualOrders = orderService.getAllOrders();

        // Then
        assertEquals(2, actualOrders.size());
        assertEquals("Order Placed", actualOrders.get(0).getStatus());
        assertEquals("Order Processing", actualOrders.get(1).getStatus());
        verify(orderRepository).findAll();
    }

    @Test
    void testGetOrderById() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Optional<Order> result = orderService.getOrderById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("PENDING", result.get().getStatus());
        verify(orderRepository).findById(1L);
    }

    @Test
    void testGetOrderByIdNotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderService.getOrderById(999L);

        // Then
        assertTrue(result.isEmpty());
        verify(orderRepository).findById(999L);
    }

    @Test
    void testDeleteOrder() {
        // Given
        doNothing().when(orderRepository).deleteById(1L);

        // When
        orderService.deleteOrder(1L);

        // Then
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void testUpdateOrderStatus() {
        // Given
        Driver mockDriver = new Driver();
        mockDriver.setDriverId(1L);
        mockDriver.setDriverName("Test Driver");
        mockDriver.setVehicle("Van");
        
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setStatus("Order Processing");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(driverRepository.findAll()).thenReturn(Arrays.asList(mockDriver));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        // When
        Order result = orderService.updateOrderStatus(1L, "Order Processing");

        // Then
        assertEquals("Order Processing", result.getStatus());
        verify(orderRepository).findById(1L);
        verify(driverRepository).findAll();
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatusNotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(999L, "Order Processing");
        });

        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetCurrentOrderStage() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        String stage = orderService.getCurrentOrderStage(1L);

        // Then
        assertNotNull(stage);
        verify(orderRepository).findById(1L);
    }

    @Test
    void testGetCurrentOrderStageNotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        String stage = orderService.getCurrentOrderStage(999L);

        // Then
        assertEquals("Order not found", stage);
        verify(orderRepository).findById(999L);
    }
}
