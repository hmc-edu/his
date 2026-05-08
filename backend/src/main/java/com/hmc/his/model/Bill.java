package com.hmc.his.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Bill {
    private Long id;
    private String billNo;
    private Long prescriptionId;
    private BigDecimal totalAmount;
    /** PENDING / PAID / REFUNDED */
    private String status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 关联展示字段（非数据库列） */
    private List<BillItem> items;
    private String patientName;
    private String patientGender;
    private String doctorName;
    private String regNo;
}
