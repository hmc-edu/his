package com.hmc.his.service;

import com.hmc.his.dto.RegistrationCreateReq;
import com.hmc.his.model.Registration;

import java.time.LocalDate;
import java.util.List;

public interface RegistrationService {
    Registration create(RegistrationCreateReq req);

    List<Registration> list(LocalDate regDate, String status, Long doctorId);

    Registration get(Long id);

    void cancel(Long id);

    /** 由 visit service 调用：状态切换。 */
    void changeStatus(Long id, String status);
}
