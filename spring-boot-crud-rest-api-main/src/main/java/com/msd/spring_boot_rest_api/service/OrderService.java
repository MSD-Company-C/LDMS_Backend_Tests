package com.msd.spring_boot_rest_api.service;

import com.msd.spring_boot_rest_api.model.Order;
import com.msd.spring_boot_rest_api.repository.OrderRepository;
import org.springframework.stereotype.Service;
import com.msd.spring_boot_rest_api.model.Driver;
import com.msd.spring_boot_rest_api.repository.DriverRepository;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final DriverRepository driverRepository;

    public OrderService(OrderRepository repository, DriverRepository driverRepository) {
        this.repository = repository;
        this.driverRepository = driverRepository;
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return repository.findById(id);
    }

    public void deleteOrder(Long id) {
        repository.deleteById(id);
    }

    // Get current stage of a specific order
    public String getCurrentOrderStage(Long orderId) {
        Optional<Order> orderOpt = repository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return "Order not found";
        }

        Order order = orderOpt.get();

        Map<String, LocalDateTime> stages = new HashMap<>();
        stages.put("Order Placed", order.getOrderPlaced().getTimestamp());
        stages.put("Order Processing", order.getOrderProcessing().getTimestamp());
        stages.put("Out for Delivery", order.getOrderOutForDelivery().getTimestamp());
        stages.put("Order Delivered", order.getOrderDelivered().getTimestamp());

        String currentStage = "Order Placed";
        LocalDateTime latestTime = null;

        for (Map.Entry<String, LocalDateTime> entry : stages.entrySet()) {
            LocalDateTime time = entry.getValue();
            if (time != null && (latestTime == null || time.isAfter(latestTime))) {
                latestTime = time;
                currentStage = entry.getKey();
            }
        }

        return currentStage;
    }

    public List<Order> getPastOrders() {
        LocalDate today = LocalDate.now();
        return repository.findAll().stream()
                .filter(order -> {
                    LocalDateTime processingTime = order.getOrderProcessing().getTimestamp();
                    return processingTime != null && processingTime.toLocalDate().isBefore(today);
                })
                .collect(Collectors.toList());
    }

    public List<Order> getTodayOrders() {
        LocalDate today = LocalDate.now();
        return repository.findAll().stream()
                .filter(order -> {
                    LocalDateTime processingTime = order.getOrderProcessing().getTimestamp();
                    return processingTime != null && processingTime.toLocalDate().isEqual(today);
                })
                .collect(Collectors.toList());
    }

    public List<Order> getUpcomingOrders() {
        LocalDate today = LocalDate.now();
        return repository.findAll().stream()
                .filter(order -> {
                    LocalDateTime processingTime = order.getOrderProcessing().getTimestamp();
                    return processingTime != null && processingTime.toLocalDate().isAfter(today);
                })
                .collect(Collectors.toList());
    }

    public List<Order> getCurrentlyPlacedOrders() {
        return repository.findAll().stream()
                .filter(order -> order.getOrderPlaced().getTimestamp() != null &&
                        (order.getOrderProcessing() == null || order.getOrderProcessing().getTimestamp() == null) &&
                        (order.getOrderOutForDelivery() == null
                                || order.getOrderOutForDelivery().getTimestamp() == null)
                        &&
                        (order.getOrderDelivered() == null || order.getOrderDelivered().getTimestamp() == null))
                .collect(Collectors.toList());
    }

    public Order updateOrderStatus(Long orderId, String newStatus) {
        Optional<Order> orderOpt = repository.findById(orderId);

        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        Order order = orderOpt.get();

        switch (newStatus) {
            case "Order Placed":
                // Set completed = true, which will also auto-set timestamp if not set
                order.getOrderPlaced().setCompleted(true);

                // Get the timestamp that was set
                LocalDateTime placedTime = order.getOrderPlaced().getTimestamp();

                // Add 2.5 days = 2 days + 12 hours
                LocalDateTime estimatedDelivery = placedTime.plusDays(2).plusHours(12);

                // Convert to String if your estimatedDelivery is a String column (as in your
                // entity)
                order.setEstimatedDelivery(estimatedDelivery.toString());
                order.setStatus("Order Placed");
                break;

            case "Order Processing":
                order.getOrderProcessing().setCompleted(true);

                if (order.getDriver() == null) {
                    List<Driver> allDrivers = driverRepository.findAll();

                    if (allDrivers.isEmpty()) {
                        throw new RuntimeException("No available drivers");
                    }

                    // Pick a random driver
                    Random rand = new Random();
                    Driver randomDriver = allDrivers.get(rand.nextInt(allDrivers.size()));

                    order.setDriver(randomDriver);
                }

                order.setStatus("Order Processing");
                break;
            case "Out for Delivery":
                order.getOrderOutForDelivery().setCompleted(true);
                order.setStatus("Out for Delivery");
                break;
            case "Order Delivered":
                order.getOrderDelivered().setCompleted(true);
                order.setStatus("Order Delivered");
                break;
            default:
                throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        return repository.save(order);
    }

    public List<Order> getOrdersByStatus(String status) {
        return repository.findAll().stream()
                .filter(order -> status.equalsIgnoreCase(order.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Order> getOrdersByStatus(List<String> of) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOrdersByStatus'");
    }

    public List<Order> getOrdersByStatuses(List<String> statuses) {
        return repository.findAll().stream()
                .filter(order -> statuses.contains(order.getStatus()))
                .collect(Collectors.toList());
    }

}
