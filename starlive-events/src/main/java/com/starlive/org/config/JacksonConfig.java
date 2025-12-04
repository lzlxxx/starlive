package com.starlive.org.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import co.elastic.clients.elasticsearch._types.GeoLocation;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // 配置 ObjectMapper
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // 忽略未知字段
        
        // 注册自定义序列化器和反序列化器
        SimpleModule module = new SimpleModule();
        module.addSerializer(GeoLocation.class, new GeoLocationJsonSerializer());
        module.addDeserializer(GeoLocation.class, new GeoLocationJsonDeserializer());
        mapper.registerModule(module);
        
        return mapper;
    }
} 