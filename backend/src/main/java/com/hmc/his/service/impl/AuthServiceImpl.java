package com.hmc.his.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.hmc.his.common.BusinessException;
import com.hmc.his.dto.LoginReq;
import com.hmc.his.dto.LoginRes;
import com.hmc.his.model.SysUser;
import com.hmc.his.repository.SysUserRepository;
import com.hmc.his.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public LoginRes login(LoginReq req) {
        SysUser user = userRepository.selectByUsername(req.getUsername());
        if (user == null || !Boolean.TRUE.equals(user.getEnabled())) {
            throw new BusinessException("用户不存在或已禁用");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        StpUtil.login(user.getId());
        StpUtil.getSession().set("user", user);

        SaTokenInfo info = StpUtil.getTokenInfo();
        LoginRes res = new LoginRes();
        res.setTokenName(info.getTokenName());
        res.setTokenValue(info.getTokenValue());
        res.setUserInfo(toUserInfo(user));
        return res;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public LoginRes.UserInfo currentUser() {
        long id = StpUtil.getLoginIdAsLong();
        SysUser user = userRepository.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toUserInfo(user);
    }

    private LoginRes.UserInfo toUserInfo(SysUser user) {
        LoginRes.UserInfo info = new LoginRes.UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setRealName(user.getRealName());
        info.setRole(user.getRole());
        return info;
    }
}
