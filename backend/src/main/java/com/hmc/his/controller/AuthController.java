package com.hmc.his.controller;

import com.hmc.his.common.R;
import com.hmc.his.dto.LoginReq;
import com.hmc.his.dto.LoginRes;
import com.hmc.his.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public R<LoginRes> login(@RequestBody @Valid LoginReq req) {
        return R.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    @GetMapping("/me")
    public R<LoginRes.UserInfo> me() {
        return R.ok(authService.currentUser());
    }
}
