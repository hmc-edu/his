package com.hmc.his.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationCreateReq {
    @NotNull(message = "请选择患者")
    private Long patientId;

    @NotNull(message = "请选择医生")
    private Long doctorId;
}
