package com.hmc.his.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.hmc.his.common.PageRes;
import com.hmc.his.common.R;
import com.hmc.his.dto.UserSaveReq;
import com.hmc.his.model.SysUser;
import com.hmc.his.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "系统-用户")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class UserController {

    private final UserService userService;

    @GetMapping
    public R<PageRes<SysUser>> page(@RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return R.ok(userService.page(keyword, page, size));
    }

    @GetMapping("/{id}")
    public R<SysUser> get(@PathVariable Long id) {
        return R.ok(userService.get(id));
    }

    @PostMapping
    public R<Long> create(@RequestBody @Valid UserSaveReq req) {
        return R.ok(userService.create(req));
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody @Valid UserSaveReq req) {
        userService.update(id, req);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return R.ok();
    }
}
