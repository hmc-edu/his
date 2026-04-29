package com.hmc.his.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Registration {
    private Long id;
    private String regNo;
    private Long patientId;
    private Long doctorId;
    private Long deptId;
    private LocalDate regDate;
    private BigDecimal regFee;
    /** WAITING / VISITING / DONE / CANCELLED */
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 关联展示字段（非数据库列） */
    private String patientName;
    private String patientGender;
    private String doctorName;
    private String deptName;
}
