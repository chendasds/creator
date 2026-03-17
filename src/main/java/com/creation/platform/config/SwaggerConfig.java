package com.creation.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("个人创作记录与作品分享平台 - 接口文档")
                        .description("包含用户、作品、评论、标签等核心业务接口的在线调试页面")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Creation Platform")
                                .email("support@creation-platform.com")));
    }
}
