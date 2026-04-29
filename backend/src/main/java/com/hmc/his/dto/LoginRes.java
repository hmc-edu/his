package com.hmc.his.dto;

import lombok.Data;

@Data
public class LoginRes {
    private String tokenName;
    private String tokenValue;
    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String realName;
        private String role;
    }
}
