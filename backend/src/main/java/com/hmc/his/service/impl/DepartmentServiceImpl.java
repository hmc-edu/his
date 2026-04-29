package com.hmc.his.service.impl;

import com.hmc.his.common.BusinessException;
import com.hmc.his.model.Department;
import com.hmc.his.repository.DepartmentRepository;
import com.hmc.his.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<Department> list() {
        return departmentRepository.selectAll();
    }

    @Override
    public Department get(Long id) {
        Department d = departmentRepository.selectById(id);
        if (d == null) throw new BusinessException("科室不存在");
        return d;
    }

    @Override
    public Long create(Department dept) {
        if (dept.getSort() == null) dept.setSort(0);
        departmentRepository.insert(dept);
        return dept.getId();
    }

    @Override
    public void update(Long id, Department dept) {
        Department exist = departmentRepository.selectById(id);
        if (exist == null) throw new BusinessException("科室不存在");
        dept.setId(id);
        if (dept.getSort() == null) dept.setSort(exist.getSort());
        departmentRepository.update(dept);
    }

    @Override
    public void delete(Long id) {
        departmentRepository.deleteById(id);
    }
}
