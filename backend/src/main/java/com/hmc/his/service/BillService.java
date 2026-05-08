package com.hmc.his.service;

import com.hmc.his.model.Bill;
import com.hmc.his.model.Prescription;

import java.util.List;

public interface BillService {
    Bill get(Long id);

    List<Bill> list(String status);

    void charge(Long id);

    void refund(Long id);

    /** 由 VisitService 调用：处方提交后自动生成账单 */
    Bill createFromPrescription(Prescription prescription);
}
