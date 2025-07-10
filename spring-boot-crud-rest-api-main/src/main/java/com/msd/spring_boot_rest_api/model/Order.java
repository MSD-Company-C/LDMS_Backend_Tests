package com.msd.spring_boot_rest_api.model;

import jakarta.persistence.*;


@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "estimated_delivery", nullable = false)
    private String estimatedDelivery;

    @ManyToOne
    @JoinColumn
    (name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "driver_id",nullable = true)
    private Driver driver;

    // 4 Order Stages with AttributeOverrides
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "timestamp", column = @Column(name = "placed_time")),
            @AttributeOverride(name = "completed", column = @Column(name = "placed_completed"))
    })
    private OrderStage orderPlaced = new OrderStage();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "timestamp", column = @Column(name = "processing_time")),
            @AttributeOverride(name = "completed", column = @Column(name = "processing_completed"))
    })
    private OrderStage orderProcessing = new OrderStage();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "timestamp", column = @Column(name = "out_for_delivery_time")),
            @AttributeOverride(name = "completed", column = @Column(name = "out_for_delivery_completed"))
    })
    private OrderStage orderOutForDelivery = new OrderStage();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "timestamp", column = @Column(name = "delivered_time")),
            @AttributeOverride(name = "completed", column = @Column(name = "delivered_completed"))
    })
    private OrderStage orderDelivered = new OrderStage();




    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;   

}
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }
    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public Driver getDriver() {
        return driver;
    }
    public void setDriver(Driver driver) {
        this.driver = driver;
    }


    // Getters & Setters (only showing stage-related for brevity)
    public OrderStage getOrderPlaced() {
        return orderPlaced;
    }

    public void setOrderPlaced(OrderStage orderPlaced) {
        this.orderPlaced = orderPlaced;
    }

    public OrderStage getOrderProcessing() {
        return orderProcessing;
    }

    public void setOrderProcessing(OrderStage orderProcessing) {
        this.orderProcessing = orderProcessing;
    }

    public OrderStage getOrderOutForDelivery() {
        return orderOutForDelivery;
    }

    public void setOrderOutForDelivery(OrderStage orderOutForDelivery) {
        this.orderOutForDelivery = orderOutForDelivery;
    }

    public OrderStage getOrderDelivered() {
        return orderDelivered;
    }

    public void setOrderDelivered(OrderStage orderDelivered) {
        this.orderDelivered = orderDelivered;
    }

    // Other fields like status, estimatedDelivery, customer, etc. not shown
}   
