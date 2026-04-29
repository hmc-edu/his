package com.hmc.his.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hisOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("门诊 HIS 演示系统 API")
                .version("0.0.1")
                .description("教学用门诊 HIS 系统接口文档"));
    }
}
