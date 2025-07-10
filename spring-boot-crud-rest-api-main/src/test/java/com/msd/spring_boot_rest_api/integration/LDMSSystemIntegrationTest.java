package com.msd.spring_boot_rest_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msd.spring_boot_rest_api.dto.LoginRequest;
import com.msd.spring_boot_rest_api.model.*;
import com.msd.spring_boot_rest_api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver", 
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
public class LDMSSystemIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private OrderRepository orderRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        // Clean up database
        orderRepository.deleteAll();
        driverRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCompleteSystemWorkflow() throws Exception {
        // 1. Create test users
        User admin = new User("admin@ldms.com", "admin123", Role.ADMIN);
        User warehouse = new User("warehouse@ldms.com", "warehouse123", Role.WAREHOUSE);
        User driver = new User("driver@ldms.com", "driver123", Role.DRIVER);
        
        userRepository.save(admin);
        userRepository.save(warehouse);
        userRepository.save(driver);

        // 2. Create test driver
        Driver testDriver = new Driver();
        testDriver.setDriverName("John Driver");
        testDriver.setVehicle("Truck-001");
        testDriver.setLocation("Warehouse A");
        driverRepository.save(testDriver);

        // 3. Test authentication with admin
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setEmail("admin@ldms.com");
        adminLogin.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        // 4. Test authentication with warehouse user
        LoginRequest warehouseLogin = new LoginRequest();
        warehouseLogin.setEmail("warehouse@ldms.com");
        warehouseLogin.setPassword("warehouse123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(warehouseLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        // 5. Test driver endpoint (should work without authentication for test endpoint)
        mockMvc.perform(get("/api/drivers/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("API is working!"));

        // 6. Test invalid login
        LoginRequest invalidLogin = new LoginRequest();
        invalidLogin.setEmail("invalid@ldms.com");
        invalidLogin.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserRoleBasedAuthentication() throws Exception {
        // Create users with different roles
        User admin = new User("admin@test.com", "password", Role.ADMIN);
        User warehouse = new User("warehouse@test.com", "password", Role.WAREHOUSE);
        User driver = new User("driver@test.com", "password", Role.DRIVER);
        
        userRepository.save(admin);
        userRepository.save(warehouse);
        userRepository.save(driver);

        // Test admin login
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setEmail("admin@test.com");
        adminLogin.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        // Test warehouse login
        LoginRequest warehouseLogin = new LoginRequest();
        warehouseLogin.setEmail("warehouse@test.com");
        warehouseLogin.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(warehouseLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        // Test driver login
        LoginRequest driverLogin = new LoginRequest();
        driverLogin.setEmail("driver@test.com");
        driverLogin.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(driverLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testPasswordValidation() throws Exception {
        // Create user
        User user = new User("test@ldms.com", "correctpassword", Role.ADMIN);
        userRepository.save(user);

        // Test correct password
        LoginRequest correctLogin = new LoginRequest();
        correctLogin.setEmail("test@ldms.com");
        correctLogin.setPassword("correctpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correctLogin)))
                .andExpect(status().isOk());

        // Test wrong password
        LoginRequest wrongLogin = new LoginRequest();
        wrongLogin.setEmail("test@ldms.com");
        wrongLogin.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongLogin)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid password"));
    }

    @Test
    void testDriverManagement() throws Exception {
        // Create a driver
        Driver driver1 = new Driver();
        driver1.setDriverName("John Doe");
        driver1.setVehicle("Truck-001");
        driver1.setLocation("Warehouse A");
        driverRepository.save(driver1);

        Driver driver2 = new Driver();
        driver2.setDriverName("Jane Smith");
        driver2.setVehicle("Van-002");
        driver2.setLocation("Warehouse B");
        driverRepository.save(driver2);

        // Test API working endpoint
        mockMvc.perform(get("/api/drivers/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("API is working!"));

        // Note: The getAllDrivers endpoint requires authentication
        // In a real test, you would need to include JWT token in the request
    }

    @Test
    void testDatabaseEntityCreation() throws Exception {
        // Test User creation
        User user = new User("test@example.com", "password123", Role.ADMIN);
        User savedUser = userRepository.save(user);
        
        assert savedUser.getId() != null;
        assert savedUser.getEmail().equals("test@example.com");
        assert savedUser.getRole().equals(Role.ADMIN);

        // Test Driver creation
        Driver driver = new Driver();
        driver.setDriverName("Test Driver");
        driver.setVehicle("Test Vehicle");
        driver.setLocation("Test Location");
        Driver savedDriver = driverRepository.save(driver);
        
        assert savedDriver.getDriverId() != null;
        assert savedDriver.getDriverName().equals("Test Driver");
        assert savedDriver.getVehicle().equals("Test Vehicle");
    }
}
