package com.msd.spring_boot_rest_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "drivers")
 // This annotation indicates that this class can be embedded in other entities
public class Driver {
    @Id
    @Column(name = "driver_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate ID
    private Long driverId;

    @Column (name = "driver_name", nullable = false)    
    private String driverName;

    @Column(name = "vehicle", nullable = false)
    private String vehicle;

    @Column(name = "driver_location", nullable = true)
    private String location;


}
