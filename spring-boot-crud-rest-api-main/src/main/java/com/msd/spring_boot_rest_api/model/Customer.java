package com.msd.spring_boot_rest_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class Customer {
    @Id
    @Column(name = "customer_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate ID
    private Long customerId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_address", nullable = false)
    private String customerAddress;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Enumerated(EnumType.STRING) // store enum name as string ("USER", "ADMIN", etc.)
    @Column(name = "role", nullable = false)
    private Role role;


    
}
