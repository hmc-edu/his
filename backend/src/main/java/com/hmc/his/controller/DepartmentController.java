package com.hmc.his.controller;

import com.hmc.his.common.R;
import com.hmc.his.model.Department;
import com.hmc.his.service.DepartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "基础信息-科室")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public R<List<Department>> list() {
        return R.ok(departmentService.list());
    }

    @GetMapping("/{id}")
    public R<Department> get(@PathVariable Long id) {
        return R.ok(departmentService.get(id));
    }

    @PostMapping
    public R<Long> create(@RequestBody Department dept) {
        return R.ok(departmentService.create(dept));
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Department dept) {
        departmentService.update(id, dept);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return R.ok();
    }
}
