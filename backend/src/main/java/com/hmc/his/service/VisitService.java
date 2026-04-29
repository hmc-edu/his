package com.hmc.his.service;

import com.hmc.his.dto.PrescriptionCreateReq;
import com.hmc.his.dto.VisitStartReq;
import com.hmc.his.dto.VisitUpdateReq;
import com.hmc.his.model.Prescription;
import com.hmc.his.model.Registration;
import com.hmc.his.model.Visit;

import java.util.List;

public interface VisitService {
    /** 当前医生 WAITING 队列 */
    List<Registration> queue(Long doctorId);

    /** 接诊：创建 visit，挂号单置 VISITING */
    Visit start(VisitStartReq req);

    Visit get(Long id);

    void update(Long id, VisitUpdateReq req);

    /** 完成：挂号单置 DONE */
    void finish(Long id);

    Prescription prescribe(Long visitId, PrescriptionCreateReq req);
}
