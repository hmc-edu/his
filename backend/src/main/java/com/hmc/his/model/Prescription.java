package com.hmc.his.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Prescription {
    private Long id;
    private Long visitId;
    private LocalDateTime prescribedAt;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 关联明细（非数据库列） */
    private List<PrescriptionItem> items;
}
