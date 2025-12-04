package com.starlive.org.service;

import com.starlive.org.util.RedisUtil;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Slf4j
@Service
public class TestService {
    @Autowired
    RedisUtil redisUtil;

    /**
     * 业务
     */
    public void testRedisUtil() {
        redisUtil.set("aaa","bbb");
        log.info( (String)redisUtil.get("aaa"));
        log.info("hello world");
    }
}
