package com.hmc.his.service;

import com.hmc.his.model.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> list();

    Department get(Long id);

    Long create(Department dept);

    void update(Long id, Department dept);

    void delete(Long id);
}
