package com.hmc.his.controller;

import com.hmc.his.common.R;
import com.hmc.his.dto.PrescriptionCreateReq;
import com.hmc.his.dto.VisitStartReq;
import com.hmc.his.dto.VisitUpdateReq;
import com.hmc.his.model.Prescription;
import com.hmc.his.model.Registration;
import com.hmc.his.model.Visit;
import com.hmc.his.service.VisitService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "医生工作站-就诊")
@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @GetMapping("/queue")
    public R<List<Registration>> queue(@RequestParam(required = false) Long doctorId) {
        return R.ok(visitService.queue(doctorId));
    }

    @PostMapping
    public R<Visit> start(@RequestBody @Valid VisitStartReq req) {
        return R.ok(visitService.start(req));
    }

    @GetMapping("/{id}")
    public R<Visit> get(@PathVariable Long id) {
        return R.ok(visitService.get(id));
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody VisitUpdateReq req) {
        visitService.update(id, req);
        return R.ok();
    }

    @PostMapping("/{id}/finish")
    public R<Void> finish(@PathVariable Long id) {
        visitService.finish(id);
        return R.ok();
    }

    @PostMapping("/{id}/prescriptions")
    public R<Prescription> prescribe(@PathVariable Long id, @RequestBody @Valid PrescriptionCreateReq req) {
        return R.ok(visitService.prescribe(id, req));
    }
}
