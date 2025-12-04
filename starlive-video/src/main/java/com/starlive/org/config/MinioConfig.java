package com.starlive.org.config;

import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Resource
    private MinioConfigInfo minioConfigInfo;
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioConfigInfo.getEndpoint())
                .credentials(minioConfigInfo.getAccessKey(), minioConfigInfo.getSecretKey())
                .build();
    }
}
