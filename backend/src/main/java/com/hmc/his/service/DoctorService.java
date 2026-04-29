package com.hmc.his.service;

import com.hmc.his.model.Doctor;

import java.util.List;

public interface DoctorService {
    List<Doctor> list(Long deptId);

    Doctor get(Long id);

    Doctor getByUserId(Long userId);

    Long create(Doctor doctor);

    void update(Long id, Doctor doctor);

    void delete(Long id);
}
