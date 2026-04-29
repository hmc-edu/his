package com.hmc.his.service.impl;

import com.hmc.his.common.BusinessException;
import com.hmc.his.dto.PrescriptionCreateReq;
import com.hmc.his.dto.VisitStartReq;
import com.hmc.his.dto.VisitUpdateReq;
import com.hmc.his.model.*;
import com.hmc.his.repository.PrescriptionRepository;
import com.hmc.his.repository.VisitRepository;
import com.hmc.his.service.DrugService;
import com.hmc.his.service.RegistrationService;
import com.hmc.his.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final RegistrationService registrationService;
    private final DrugService drugService;

    @Override
    public List<Registration> queue(Long doctorId) {
        return registrationService.list(java.time.LocalDate.now(), "WAITING", doctorId);
    }

    @Override
    @Transactional
    public Visit start(VisitStartReq req) {
        Registration reg = registrationService.get(req.getRegistrationId());
        if (!"WAITING".equals(reg.getStatus())) {
            throw new BusinessException("仅待诊状态的挂号可接诊");
        }
        Visit existing = visitRepository.selectByRegistrationId(reg.getId());
        if (existing != null) {
            return loadFull(existing.getId());
        }
        Visit v = new Visit();
        v.setRegistrationId(reg.getId());
        v.setVisitedAt(LocalDateTime.now());
        visitRepository.insert(v);
        registrationService.changeStatus(reg.getId(), "VISITING");
        return loadFull(v.getId());
    }

    @Override
    public Visit get(Long id) {
        return loadFull(id);
    }

    @Override
    public void update(Long id, VisitUpdateReq req) {
        Visit v = visitRepository.selectById(id);
        if (v == null) throw new BusinessException("就诊记录不存在");
        v.setChiefComplaint(req.getChiefComplaint());
        v.setPresentIllness(req.getPresentIllness());
        v.setDiagnosis(req.getDiagnosis());
        v.setDoctorAdvice(req.getDoctorAdvice());
        visitRepository.update(v);
    }

    @Override
    @Transactional
    public void finish(Long id) {
        Visit v = visitRepository.selectById(id);
        if (v == null) throw new BusinessException("就诊记录不存在");
        registrationService.changeStatus(v.getRegistrationId(), "DONE");
    }

    @Override
    @Transactional
    public Prescription prescribe(Long visitId, PrescriptionCreateReq req) {
        Visit v = visitRepository.selectById(visitId);
        if (v == null) throw new BusinessException("就诊记录不存在");

        Prescription p = new Prescription();
        p.setVisitId(visitId);
        p.setPrescribedAt(LocalDateTime.now());
        p.setTotalAmount(BigDecimal.ZERO);
        prescriptionRepository.insert(p);

        BigDecimal total = BigDecimal.ZERO;
        List<PrescriptionItem> savedItems = new ArrayList<>();
        for (PrescriptionCreateReq.Item raw : req.getItems()) {
            Drug drug = drugService.get(raw.getDrugId());
            int qty = raw.getQty() == null ? 1 : raw.getQty();
            int days = raw.getDays() == null ? 1 : raw.getDays();
            BigDecimal subtotal = drug.getPrice().multiply(BigDecimal.valueOf(qty));

            PrescriptionItem item = new PrescriptionItem();
            item.setPrescriptionId(p.getId());
            item.setDrugId(drug.getId());
            item.setDosage(raw.getDosage());
            item.setFrequency(raw.getFrequency());
            item.setDays(days);
            item.setQty(qty);
            item.setUnitPrice(drug.getPrice());
            item.setSubtotal(subtotal);
            prescriptionRepository.insertItem(item);
            savedItems.add(item);
            total = total.add(subtotal);
        }
        p.setTotalAmount(total);
        p.setItems(savedItems);
        prescriptionRepository.updateTotal(p.getId(), total);
        return p;
    }

    private Visit loadFull(Long id) {
        Visit v = visitRepository.selectById(id);
        if (v == null) throw new BusinessException("就诊记录不存在");
        Registration reg = registrationService.get(v.getRegistrationId());
        v.setRegistration(reg);
        List<Prescription> ps = prescriptionRepository.selectByVisitId(id);
        for (Prescription p : ps) {
            p.setItems(prescriptionRepository.selectItems(p.getId()));
        }
        v.setPrescriptions(ps);
        return v;
    }
}
