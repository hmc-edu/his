package com.hmc.his.service.impl;

import com.hmc.his.common.BusinessException;
import com.hmc.his.model.Bill;
import com.hmc.his.model.BillItem;
import com.hmc.his.model.Prescription;
import com.hmc.his.model.PrescriptionItem;
import com.hmc.his.repository.BillRepository;
import com.hmc.his.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    @Override
    public Bill get(Long id) {
        Bill b = billRepository.selectById(id);
        if (b == null) throw new BusinessException("账单不存在");
        b.setItems(billRepository.selectItems(id));
        return b;
    }

    @Override
    public List<Bill> list(String status) {
        return billRepository.selectList(status);
    }

    @Override
    @Transactional
    public void charge(Long id) {
        Bill b = billRepository.selectById(id);
        if (b == null) throw new BusinessException("账单不存在");
        if (!"PENDING".equals(b.getStatus())) {
            throw new BusinessException("仅待收费状态的账单可收费");
        }
        billRepository.updateStatus(id, "PAID", LocalDateTime.now());
    }

    @Override
    @Transactional
    public void refund(Long id) {
        Bill b = billRepository.selectById(id);
        if (b == null) throw new BusinessException("账单不存在");
        if (!"PAID".equals(b.getStatus())) {
            throw new BusinessException("仅已收费状态的账单可退费");
        }
        billRepository.updateStatus(id, "REFUNDED", null);
    }

    @Override
    public Bill createFromPrescription(Prescription prescription) {
        Bill b = new Bill();
        b.setPrescriptionId(prescription.getId());
        b.setTotalAmount(prescription.getTotalAmount());
        b.setStatus("PENDING");
        b.setBillNo(generateBillNo());
        billRepository.insert(b);

        for (PrescriptionItem pi : prescription.getItems()) {
            BillItem bi = new BillItem();
            bi.setBillId(b.getId());
            bi.setPrescriptionItemId(pi.getId());
            bi.setDrugId(pi.getDrugId());
            bi.setQty(pi.getQty());
            bi.setUnitPrice(pi.getUnitPrice());
            bi.setSubtotal(pi.getSubtotal());
            billRepository.insertItem(bi);
        }
        return b;
    }

    private String generateBillNo() {
        long seq = billRepository.countToday() + 1;
        String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "B" + prefix + String.format("%04d", seq);
    }
}
