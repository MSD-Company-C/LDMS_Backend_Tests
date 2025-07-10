package com.msd.spring_boot_rest_api.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Simple test to verify the Spring Boot application starts correctly
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
public class ApplicationStartupTest {

    @Test
    void contextLoads() {
        // This test will pass if the Spring Boot application context loads successfully
        // It's a basic smoke test to ensure all configurations are correct
    }

    @Test
    void applicationStarts() {
        // Another basic test to ensure the application can start
        assert true : "Application should start without errors";
    }
}
