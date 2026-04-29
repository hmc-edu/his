package com.hmc.his.dto;

import lombok.Data;

@Data
public class VisitUpdateReq {
    private String chiefComplaint;
    private String presentIllness;
    private String diagnosis;
    private String doctorAdvice;
}
