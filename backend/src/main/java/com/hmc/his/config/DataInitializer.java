package com.hmc.his.config;

import com.hmc.his.model.Doctor;
import com.hmc.his.model.SysUser;
import com.hmc.his.repository.DoctorRepository;
import com.hmc.his.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 启动时初始化默认账号与医生记录。
 * 仅当 sys_user 为空时插入，避免重复执行。
 * 学生扩展提示：可在此基础上加入更多默认数据，或读取外部配置驱动初始化。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.countAll() > 0) {
            log.info("sys_user 已存在数据，跳过默认账号初始化");
            return;
        }

        log.info("初始化默认账号：admin / reception / doctor，密码均为 123456");
        SysUser admin = newUser("admin", "123456", "系统管理员", "ADMIN");
        SysUser reception = newUser("reception", "123456", "前台挂号员", "RECEPTION");
        SysUser doctorUser = newUser("doctor", "123456", "张医生", "DOCTOR");
        userRepository.insert(admin);
        userRepository.insert(reception);
        userRepository.insert(doctorUser);

        if (doctorRepository.countAll() == 0) {
            log.info("初始化示例医生记录");
            doctorRepository.insert(buildDoctor(doctorUser.getId(), 1L, "张医生", "主任医师", new BigDecimal("15.00")));
            doctorRepository.insert(buildDoctor(null, 2L, "李医生", "副主任医师", new BigDecimal("12.00")));
            doctorRepository.insert(buildDoctor(null, 3L, "王医生", "主治医师", new BigDecimal("10.00")));
        }
    }

    private SysUser newUser(String username, String rawPwd, String realName, String role) {
        SysUser u = new SysUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPwd));
        u.setRealName(realName);
        u.setRole(role);
        u.setEnabled(true);
        return u;
    }

    private Doctor buildDoctor(Long userId, Long deptId, String name, String title, BigDecimal regFee) {
        Doctor d = new Doctor();
        d.setUserId(userId);
        d.setDeptId(deptId);
        d.setName(name);
        d.setTitle(title);
        d.setRegFee(regFee);
        return d;
    }
}
