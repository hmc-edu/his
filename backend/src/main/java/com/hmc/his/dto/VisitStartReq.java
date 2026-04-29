package com.hmc.his.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VisitStartReq {
    @NotNull(message = "请选择挂号单")
    private Long registrationId;
}
