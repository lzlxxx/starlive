package com.starlive.org;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.starlive.org.service.IUsersService;
import org.bouncycastle.pqc.crypto.newhope.NHOtherInfoGenerator;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@MapperScan("com.starlive.org.mapper")
@SpringBootTest
public class StarliveUserTests {

    @Autowired
    private IUsersService usersService;
//    @Test
//    public void contextLoads() {
//        System.out.println(usersService.exist("1254554456"));
//    }
    @Test
    public void test() {
        System.out.println(usersService.getUser(7L).getLocation());
    }
    @Test
    public void test2() throws JsonProcessingException {
        System.out.println(usersService.getInfo("1254554456"));
    }
    @Test
    public void test3() throws JsonProcessingException {
        usersService.changeName("1254554456","zhangsan");
        System.out.println(usersService.getInfo("1254554456"));
    }
    @Test
    public void test4() throws JsonProcessingException {
        System.out.println(usersService.workInfo(13L));
    }


}

