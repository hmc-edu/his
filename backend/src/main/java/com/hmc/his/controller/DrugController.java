package com.hmc.his.controller;

import com.hmc.his.common.R;
import com.hmc.his.model.Drug;
import com.hmc.his.service.DrugService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "基础信息-药品")
@RestController
@RequestMapping("/api/drugs")
@RequiredArgsConstructor
public class DrugController {

    private final DrugService drugService;

    @GetMapping
    public R<List<Drug>> list(@RequestParam(required = false) String keyword) {
        return R.ok(drugService.list(keyword));
    }

    @GetMapping("/{id}")
    public R<Drug> get(@PathVariable Long id) {
        return R.ok(drugService.get(id));
    }

    @PostMapping
    public R<Long> create(@RequestBody Drug drug) {
        return R.ok(drugService.create(drug));
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Drug drug) {
        drugService.update(id, drug);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        drugService.delete(id);
        return R.ok();
    }
}
