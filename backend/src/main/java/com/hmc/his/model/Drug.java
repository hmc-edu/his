package com.hmc.his.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Drug {
    private Long id;
    private String code;
    private String name;
    private String spec;
    private String unit;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
