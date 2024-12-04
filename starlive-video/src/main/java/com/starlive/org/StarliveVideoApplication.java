package com.starlive.org;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMPP
@MapperScan("com.starlive.org.mapper")
public class StarliveVideoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarliveVideoApplication.class, args);
    }

}
