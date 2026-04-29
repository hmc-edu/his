package com.hmc.his.service.impl;

import com.hmc.his.common.BusinessException;
import com.hmc.his.model.Doctor;
import com.hmc.his.repository.DoctorRepository;
import com.hmc.his.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Override
    public List<Doctor> list(Long deptId) {
        return doctorRepository.selectAll(deptId);
    }

    @Override
    public Doctor get(Long id) {
        Doctor d = doctorRepository.selectById(id);
        if (d == null) throw new BusinessException("医生不存在");
        return d;
    }

    @Override
    public Doctor getByUserId(Long userId) {
        return doctorRepository.selectByUserId(userId);
    }

    @Override
    public Long create(Doctor doctor) {
        if (doctor.getRegFee() == null) doctor.setRegFee(BigDecimal.TEN);
        doctorRepository.insert(doctor);
        return doctor.getId();
    }

    @Override
    public void update(Long id, Doctor doctor) {
        Doctor exist = doctorRepository.selectById(id);
        if (exist == null) throw new BusinessException("医生不存在");
        doctor.setId(id);
        if (doctor.getRegFee() == null) doctor.setRegFee(exist.getRegFee());
        doctorRepository.update(doctor);
    }

    @Override
    public void delete(Long id) {
        doctorRepository.deleteById(id);
    }
}
