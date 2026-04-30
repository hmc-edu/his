package com.hmc.his.config;

import cn.dev33.satoken.stp.StpInterface;
import com.hmc.his.model.SysUser;
import com.hmc.his.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 角色 / 权限提供方。
 * 当 Controller 上有 @SaCheckRole("ADMIN") 等注解时，Sa-Token 会回调这里
 * 获取当前登录用户拥有哪些角色 / 权限。
 *
 * 本项目把 sys_user.role 单字段直接当成角色名（ADMIN / RECEPTION / DOCTOR），
 * 因此返回单元素列表即可。要做完整 RBAC 时改成多角色查询。
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysUserRepository sysUserRepository;

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) return Collections.emptyList();
        SysUser user = sysUserRepository.selectById(Long.valueOf(loginId.toString()));
        if (user == null || user.getRole() == null) return Collections.emptyList();
        return List.of(user.getRole());
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 当前未启用按权限点鉴权（@SaCheckPermission），返回空即可
        return Collections.emptyList();
    }
}
