package com.hmc.his.service.impl;

import com.hmc.his.common.BusinessException;
import com.hmc.his.common.PageRes;
import com.hmc.his.model.Patient;
import com.hmc.his.repository.PatientRepository;
import com.hmc.his.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public PageRes<Patient> page(String keyword, int page, int size) {
        int offset = Math.max(0, (page - 1) * size);
        List<Patient> records = patientRepository.selectPage(keyword, offset, size);
        long total = patientRepository.countPage(keyword);
        return PageRes.of(total, records);
    }

    @Override
    public Patient get(Long id) {
        Patient p = patientRepository.selectById(id);
        if (p == null) throw new BusinessException("患者不存在");
        return p;
    }

    @Override
    public Long create(Patient patient) {
        if (patient.getGender() == null || patient.getGender().isBlank()) {
            patient.setGender("未知");
        }
        patientRepository.insert(patient);
        return patient.getId();
    }

    @Override
    public void update(Long id, Patient patient) {
        Patient exist = patientRepository.selectById(id);
        if (exist == null) throw new BusinessException("患者不存在");
        patient.setId(id);
        patientRepository.update(patient);
    }

    @Override
    public void delete(Long id) {
        patientRepository.deleteById(id);
    }
}
