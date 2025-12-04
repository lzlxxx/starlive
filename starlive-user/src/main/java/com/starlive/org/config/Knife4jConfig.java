package com.starlive.org.config;

import cn.hutool.core.util.RandomUtil;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/***
 * 创建Swagger配置
 * @since:knife4j-springdoc-openapi-demo 1.0
 * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2020/03/15 20:40
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // 配置接口文档基本信息
                .info(this.getApiInfo())
                ;
    }

    @Bean("user")
    public GroupedOpenApi adminGroupApi() {
        return GroupedOpenApi.builder().group("user")
                .pathsToMatch("/user/**")
                .build();
    }


    private Info getApiInfo() {
        return new Info()
                // 配置文档标题
                .title("SpringBoot3集成Knife4j")
                // 配置文档描述
                .description("SpringBoot3集成Knife4j示例文档")
                // 配置作者信息
                .contact(new Contact().name("xxx").url("https://www.xxx.cn").email("1666397814@163.com"))
                // 配置License许可证信息
                .license(new License().name("Apache 2.0").url("https://www.xxx.cn"))
                // 概述信息
                .summary("SpringBoot3集成Knife4j")
                .termsOfService("https://www.xiezhrspace.cn")
                // 配置版本号
                .version("2.0");
    }
}

