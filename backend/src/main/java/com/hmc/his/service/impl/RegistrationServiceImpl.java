package com.hmc.his.service.impl;

import com.hmc.his.common.BusinessException;
import com.hmc.his.dto.RegistrationCreateReq;
import com.hmc.his.model.Doctor;
import com.hmc.his.model.Patient;
import com.hmc.his.model.Registration;
import com.hmc.his.repository.RegistrationRepository;
import com.hmc.his.service.DoctorService;
import com.hmc.his.service.PatientService;
import com.hmc.his.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private static final Set<String> VALID_STATUS = Set.of("WAITING", "VISITING", "DONE", "CANCELLED");

    private final RegistrationRepository registrationRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @Override
    public Registration create(RegistrationCreateReq req) {
        Patient patient = patientService.get(req.getPatientId());
        Doctor doctor = doctorService.get(req.getDoctorId());

        LocalDate today = LocalDate.now();
        Registration r = new Registration();
        r.setPatientId(patient.getId());
        r.setDoctorId(doctor.getId());
        r.setDeptId(doctor.getDeptId());
        r.setRegDate(today);
        r.setRegFee(doctor.getRegFee());
        r.setStatus("WAITING");
        r.setRegNo(generateRegNo(today));

        registrationRepository.insert(r);
        return registrationRepository.selectById(r.getId());
    }

    @Override
    public List<Registration> list(LocalDate regDate, String status, Long doctorId) {
        return registrationRepository.selectList(regDate, status, doctorId);
    }

    @Override
    public Registration get(Long id) {
        Registration r = registrationRepository.selectById(id);
        if (r == null) throw new BusinessException("挂号单不存在");
        return r;
    }

    @Override
    public void cancel(Long id) {
        Registration r = get(id);
        if (!"WAITING".equals(r.getStatus())) {
            throw new BusinessException("仅待诊状态的挂号可退号");
        }
        registrationRepository.updateStatus(id, "CANCELLED");
    }

    @Override
    public void changeStatus(Long id, String status) {
        if (!VALID_STATUS.contains(status)) {
            throw new BusinessException("非法状态: " + status);
        }
        registrationRepository.updateStatus(id, status);
    }

    private String generateRegNo(LocalDate date) {
        long seq = registrationRepository.countByDate(date) + 1;
        String prefix = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "R" + prefix + String.format("%04d", seq);
    }
}
