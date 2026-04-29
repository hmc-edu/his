package com.hmc.his.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PrescriptionCreateReq {

    @NotEmpty(message = "处方明细不能为空")
    private List<Item> items;

    @Data
    public static class Item {
        private Long drugId;
        private String dosage;
        private String frequency;
        private Integer days;
        private Integer qty;
    }
}
