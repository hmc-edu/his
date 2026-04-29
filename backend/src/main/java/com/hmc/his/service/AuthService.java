package com.hmc.his.service;

import com.hmc.his.dto.LoginReq;
import com.hmc.his.dto.LoginRes;

public interface AuthService {
    LoginRes login(LoginReq req);

    void logout();

    LoginRes.UserInfo currentUser();
}
