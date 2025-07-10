package com.msd.spring_boot_rest_api.repository;

import com.msd.spring_boot_rest_api.model.Driver;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findFirstByOrderByDriverIdAsc();

    // You can later add methods like `findByAvailableTrue()` if needed
}
