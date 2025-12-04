package com.starlive.org;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class NacosTest {
//    配置中心test
    @Value("${app.name}")
    private String appName;
    @GetMapping("/app-name")
    public String getAppName() {
        return "App Name from Nacos: " + appName;
    }
}
