package com.starlive.org.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * 创建Swagger配置
 * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
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
    @Bean("ES")
    public GroupedOpenApi ESGroupApi() {
        return GroupedOpenApi.builder().group("es")
                .pathsToMatch("/event/**")
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
