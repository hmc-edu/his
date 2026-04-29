package com.hmc.his.controller;

import com.hmc.his.common.R;
import com.hmc.his.dto.RegistrationCreateReq;
import com.hmc.his.model.Registration;
import com.hmc.his.service.RegistrationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "挂号")
@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public R<Registration> create(@RequestBody @Valid RegistrationCreateReq req) {
        return R.ok(registrationService.create(req));
    }

    @GetMapping
    public R<List<Registration>> list(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                      @RequestParam(required = false) String status,
                                      @RequestParam(required = false) Long doctorId) {
        return R.ok(registrationService.list(date, status, doctorId));
    }

    @GetMapping("/{id}")
    public R<Registration> get(@PathVariable Long id) {
        return R.ok(registrationService.get(id));
    }

    @PostMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        registrationService.cancel(id);
        return R.ok();
    }
}
