package com.hmc.his.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillItem {
    private Long id;
    private Long billId;
    private Long prescriptionItemId;
    private Long drugId;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    /** 关联展示字段 */
    private String drugName;
    private String drugSpec;
    private String drugUnit;
}
