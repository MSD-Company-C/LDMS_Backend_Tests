package com.msd.spring_boot_rest_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msd.spring_boot_rest_api.model.Driver;
import com.msd.spring_boot_rest_api.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DriverControllerTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverController driverController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(driverController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllDrivers() throws Exception {
        // Given
        Driver driver1 = new Driver();
        driver1.setDriverId(1L);
        driver1.setDriverName("John Doe");
        driver1.setVehicle("Truck-001");

        Driver driver2 = new Driver();
        driver2.setDriverId(2L);
        driver2.setDriverName("Jane Smith");
        driver2.setVehicle("Van-002");

        List<Driver> drivers = Arrays.asList(driver1, driver2);
        when(driverRepository.findAll()).thenReturn(drivers);

        // When & Then
        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].driverId").value(1))
                .andExpect(jsonPath("$[0].driverName").value("John Doe"))
                .andExpect(jsonPath("$[1].driverId").value(2))
                .andExpect(jsonPath("$[1].driverName").value("Jane Smith"));

        verify(driverRepository).findAll();
    }

    @Test
    void testApiWorking() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/drivers/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("API is working!"));

        verifyNoInteractions(driverRepository);
    }
}
