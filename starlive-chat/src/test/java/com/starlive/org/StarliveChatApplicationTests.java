package com.starlive.org;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StarliveChatApplicationTests {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Test
    public void testGetAppName() {
        // 发送 GET 请求，调用 /app-name 接口
        String url = "http://localhost:" + port + "/app-name";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // 验证返回结果
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("App Name from Nacos");
    }
    @Test
    void contextLoads() {
    }

}
