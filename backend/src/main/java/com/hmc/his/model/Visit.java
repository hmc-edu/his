package com.hmc.his.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Visit {
    private Long id;
    private Long registrationId;
    private String chiefComplaint;
    private String presentIllness;
    private String diagnosis;
    private String doctorAdvice;
    private LocalDateTime visitedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 关联展示字段（非数据库列） */
    private Registration registration;
    private List<Prescription> prescriptions;
}
