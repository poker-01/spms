package com.example.spms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 配置
 *
 * @Author SPMS
 * @Date 2026/07/03
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SPMS 智慧物业管理系统 API")
                        .description("RESTful API 接口文档")
                        .version("v1")
                        .contact(new Contact().name("SPMS Team")));
    }
}
