package com.msd.spring_boot_rest_api.controller;

import com.msd.spring_boot_rest_api.dto.LoginRequest;
import com.msd.spring_boot_rest_api.dto.LoginResponse;
import com.msd.spring_boot_rest_api.model.User;
import com.msd.spring_boot_rest_api.repository.UserRepository;
import com.msd.spring_boot_rest_api.security.JwtTokenProvider;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.msd.spring_boot_rest_api.dto.LoginResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login attempt for email: " + loginRequest.getEmail());

        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            System.out.println("User not found!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = userOpt.get();
        System.out.println("Found user: " + user.getEmail() + " with password: " + user.getPassword());

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            System.out.println("Password mismatch!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        System.out.println("Login successful!");
        String token = tokenProvider.generateToken(user.getEmail(), user.getRole());
        System.out.println("Generated JWT token: " + token);
        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }    

}
