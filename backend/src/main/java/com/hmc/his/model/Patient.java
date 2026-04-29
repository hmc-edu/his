package com.hmc.his.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Patient {
    private Long id;
    private String name;
    private String gender;
    private LocalDate birthday;
    private String phone;
    private String idCard;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
