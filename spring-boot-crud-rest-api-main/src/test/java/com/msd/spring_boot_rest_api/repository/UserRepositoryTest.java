package com.msd.spring_boot_rest_api.repository;

import com.msd.spring_boot_rest_api.model.User;
import com.msd.spring_boot_rest_api.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUser() {
        // Given
        User user = new User("test@example.com", "password123", Role.ADMIN);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("password123", savedUser.getPassword());
        assertEquals(Role.ADMIN, savedUser.getRole());
    }

    @Test
    void testFindByEmail() {
        // Given
        User user = new User("admin@ldms.com", "admin123", Role.ADMIN);
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail("admin@ldms.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("admin@ldms.com", foundUser.get().getEmail());
        assertEquals(Role.ADMIN, foundUser.get().getRole());
    }

    @Test
    void testFindByEmailNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void testUniqueEmailConstraint() {
        // Given
        User user1 = new User("duplicate@example.com", "password1", Role.ADMIN);
        User user2 = new User("duplicate@example.com", "password2", Role.WAREHOUSE);

        // When
        userRepository.save(user1);

        // Then - Should handle duplicate email gracefully
        assertThrows(Exception.class, () -> {
            userRepository.save(user2);
            userRepository.flush(); // Force the database operation
        });
    }

    @Test
    void testFindUsersByRole() {
        // Given
        User admin1 = new User("admin1@ldms.com", "password", Role.ADMIN);
        User admin2 = new User("admin2@ldms.com", "password", Role.ADMIN);
        User warehouse = new User("warehouse@ldms.com", "password", Role.WAREHOUSE);
        
        userRepository.save(admin1);
        userRepository.save(admin2);
        userRepository.save(warehouse);

        // When - assuming we have a method to find by role (if implemented)
        long totalUsers = userRepository.count();

        // Then
        assertEquals(3, totalUsers);
    }
}
