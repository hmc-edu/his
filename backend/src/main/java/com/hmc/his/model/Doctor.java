package com.hmc.his.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Doctor {
    private Long id;
    private Long userId;
    private Long deptId;
    private String name;
    private String title;
    private BigDecimal regFee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 关联展示字段（非数据库列） */
    private String deptName;
}
