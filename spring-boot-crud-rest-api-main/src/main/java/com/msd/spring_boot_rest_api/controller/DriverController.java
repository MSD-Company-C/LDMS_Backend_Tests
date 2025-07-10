package com.msd.spring_boot_rest_api.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msd.spring_boot_rest_api.model.Driver;
import com.msd.spring_boot_rest_api.repository.DriverRepository;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:3000")
public class DriverController {

    private final DriverRepository driverRepository;

    public DriverController(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
    
    @GetMapping("/test")
    public String test() {
        return "API is working!";
    }

}
