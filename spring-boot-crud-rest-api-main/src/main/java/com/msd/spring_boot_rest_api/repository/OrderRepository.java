package com.msd.spring_boot_rest_api.repository;

import com.msd.spring_boot_rest_api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
