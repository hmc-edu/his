package com.hmc.his.service;

import com.hmc.his.common.PageRes;
import com.hmc.his.model.Patient;

public interface PatientService {
    PageRes<Patient> page(String keyword, int page, int size);

    Patient get(Long id);

    Long create(Patient patient);

    void update(Long id, Patient patient);

    void delete(Long id);
}
