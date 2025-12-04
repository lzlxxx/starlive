package com.starlive.org.config;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class MinioConfigInfo {
    @Value(("${minio.config.url}"))
    private String endpoint;
    @Value(("${minio.config.accessKey}"))
    private String accessKey;
    @Value(("${minio.config.secretKey}"))
    private String secretKey;
    @Value(("${minio.config.bucketName}"))
    private String bucket;
    @Value(("${minio.config.expiry}"))
    private Integer expiry;
    @Value(("${minio.config.breakpointTime}"))
    private Integer breakpointTime;
}
