package com.hmc.his.service;

import com.hmc.his.common.PageRes;
import com.hmc.his.dto.UserSaveReq;
import com.hmc.his.model.SysUser;

public interface UserService {
    PageRes<SysUser> page(String keyword, int page, int size);

    SysUser get(Long id);

    Long create(UserSaveReq req);

    void update(Long id, UserSaveReq req);

    void delete(Long id);
}
