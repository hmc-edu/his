package com.hmc.his.controller;

import com.hmc.his.common.PageRes;
import com.hmc.his.common.R;
import com.hmc.his.model.Patient;
import com.hmc.his.service.PatientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "患者")
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public R<PageRes<Patient>> page(@RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return R.ok(patientService.page(keyword, page, size));
    }

    @GetMapping("/{id}")
    public R<Patient> get(@PathVariable Long id) {
        return R.ok(patientService.get(id));
    }

    @PostMapping
    public R<Long> create(@RequestBody Patient patient) {
        return R.ok(patientService.create(patient));
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Patient patient) {
        patientService.update(id, patient);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        patientService.delete(id);
        return R.ok();
    }
}
