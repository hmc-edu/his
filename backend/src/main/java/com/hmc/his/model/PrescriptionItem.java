package com.hmc.his.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrescriptionItem {
    private Long id;
    private Long prescriptionId;
    private Long drugId;
    private String dosage;
    private String frequency;
    private Integer days;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    /** 关联展示字段 */
    private String drugName;
    private String drugSpec;
    private String drugUnit;
}
