package com.hmc.his.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSaveReq {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 新建必填；编辑可空表示不修改密码 */
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "角色不能为空")
    private String role;

    private Boolean enabled = true;
}
