package com.hmc.his.controller;

import com.hmc.his.common.R;
import com.hmc.his.model.Doctor;
import com.hmc.his.service.DoctorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "基础信息-医生")
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public R<List<Doctor>> list(@RequestParam(required = false) Long deptId) {
        return R.ok(doctorService.list(deptId));
    }

    @GetMapping("/{id}")
    public R<Doctor> get(@PathVariable Long id) {
        return R.ok(doctorService.get(id));
    }

    @PostMapping
    public R<Long> create(@RequestBody Doctor doctor) {
        return R.ok(doctorService.create(doctor));
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Doctor doctor) {
        doctorService.update(id, doctor);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return R.ok();
    }
}
