package com.msd.spring_boot_rest_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msd.spring_boot_rest_api.dto.LoginRequest;
import com.msd.spring_boot_rest_api.model.Role;
import com.msd.spring_boot_rest_api.model.User;
import com.msd.spring_boot_rest_api.repository.UserRepository;
import com.msd.spring_boot_rest_api.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("password123");

        User user = new User("admin@test.com", "password123", Role.ADMIN);
        String token = "mock-jwt-token";

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(user));
        when(tokenProvider.generateToken(anyString(), any(Role.class))).thenReturn(token);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(userRepository).findByEmail("admin@test.com");
        verify(tokenProvider).generateToken("admin@test.com", Role.ADMIN);
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@test.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not found"));

        verify(userRepository).findByEmail("nonexistent@test.com");
        verifyNoInteractions(tokenProvider);
    }

    @Test
    void testLoginInvalidPassword() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("wrongpassword");

        User user = new User("admin@test.com", "password123", Role.ADMIN);

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid password"));

        verify(userRepository).findByEmail("admin@test.com");
        verifyNoInteractions(tokenProvider);
    }

    @Test
    void testLoginWithDifferentRoles() throws Exception {
        // Test WAREHOUSE role
        LoginRequest warehouseLogin = new LoginRequest();
        warehouseLogin.setEmail("warehouse@test.com");
        warehouseLogin.setPassword("password123");

        User warehouseUser = new User("warehouse@test.com", "password123", Role.WAREHOUSE);
        String warehouseToken = "warehouse-jwt-token";

        when(userRepository.findByEmail("warehouse@test.com")).thenReturn(Optional.of(warehouseUser));
        when(tokenProvider.generateToken("warehouse@test.com", Role.WAREHOUSE)).thenReturn(warehouseToken);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(warehouseLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(warehouseToken))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        // Test DRIVER role
        LoginRequest driverLogin = new LoginRequest();
        driverLogin.setEmail("driver@test.com");
        driverLogin.setPassword("password123");

        User driverUser = new User("driver@test.com", "password123", Role.DRIVER);
        String driverToken = "driver-jwt-token";

        when(userRepository.findByEmail("driver@test.com")).thenReturn(Optional.of(driverUser));
        when(tokenProvider.generateToken("driver@test.com", Role.DRIVER)).thenReturn(driverToken);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(driverLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(driverToken))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }
}
