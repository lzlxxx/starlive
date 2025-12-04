package com.starlive.org;

import com.starlive.org.mapper.FollowersMapper;
import com.starlive.org.mapper.UsersMapper;
import com.starlive.org.mapper.VideoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StarliveCommonInterfaceApplicationTests {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private FollowersMapper followersMapper;
    @Autowired
    UsersMapper usersMapper;


    @Test
   public void hello(){
    }


}
