package com.starlive.org;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@MapperScan("com.starlive.org.mapper")
@SpringBootApplication
public class StarliveUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarliveUserApplication.class, args);
    }

}
