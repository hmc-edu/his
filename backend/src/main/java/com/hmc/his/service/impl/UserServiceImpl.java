package com.hmc.his.service.impl;

import com.hmc.his.common.BusinessException;
import com.hmc.his.common.PageRes;
import com.hmc.his.dto.UserSaveReq;
import com.hmc.his.model.SysUser;
import com.hmc.his.repository.SysUserRepository;
import com.hmc.his.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public PageRes<SysUser> page(String keyword, int page, int size) {
        int offset = Math.max(0, (page - 1) * size);
        List<SysUser> records = userRepository.selectPage(keyword, offset, size);
        records.forEach(u -> u.setPassword(null));
        long total = userRepository.countPage(keyword);
        return PageRes.of(total, records);
    }

    @Override
    public SysUser get(Long id) {
        SysUser user = userRepository.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setPassword(null);
        return user;
    }

    @Override
    public Long create(UserSaveReq req) {
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new BusinessException("新建用户必须设置初始密码");
        }
        if (userRepository.selectByUsername(req.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRealName(req.getRealName());
        user.setRole(req.getRole());
        user.setEnabled(req.getEnabled() == null ? Boolean.TRUE : req.getEnabled());
        userRepository.insert(user);
        return user.getId();
    }

    @Override
    public void update(Long id, UserSaveReq req) {
        SysUser user = userRepository.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setRealName(req.getRealName());
        user.setRole(req.getRole());
        user.setEnabled(req.getEnabled() == null ? Boolean.TRUE : req.getEnabled());
        userRepository.update(user);

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            userRepository.updatePassword(id, passwordEncoder.encode(req.getPassword()));
        }
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
