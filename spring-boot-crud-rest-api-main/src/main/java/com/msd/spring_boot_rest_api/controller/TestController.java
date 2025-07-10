package com.msd.spring_boot_rest_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/test") // ✅ Make sure this matches your endpoint
public class TestController {

    @GetMapping("/admin")
    public String adminOnly() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return "Hello ADMIN! You are logged in as: " + auth.getName();
    }
}
