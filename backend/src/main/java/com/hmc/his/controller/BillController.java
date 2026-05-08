package com.hmc.his.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.hmc.his.common.R;
import com.hmc.his.model.Bill;
import com.hmc.his.service.BillService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "收费台")
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@SaCheckRole(value = {"ADMIN", "RECEPTION"}, mode = SaMode.OR)
public class BillController {

    private final BillService billService;

    @GetMapping
    public R<List<Bill>> list(@RequestParam(required = false) String status) {
        return R.ok(billService.list(status));
    }

    @GetMapping("/{id}")
    public R<Bill> get(@PathVariable Long id) {
        return R.ok(billService.get(id));
    }

    @PostMapping("/{id}/charge")
    public R<Void> charge(@PathVariable Long id) {
        billService.charge(id);
        return R.ok();
    }

    @PostMapping("/{id}/refund")
    public R<Void> refund(@PathVariable Long id) {
        billService.refund(id);
        return R.ok();
    }
}
