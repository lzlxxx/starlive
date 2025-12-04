package com.starlive.org.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starlive.org.vo.ExistVo;
import com.starlive.org.vo.UserVo;
import com.starlive.org.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.starlive.org.vo.WorkInfoListVo;
import com.starlive.org.vo.WorkInfoVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author meng
 * @since 2024-10-13
 */
public interface IUsersService extends IService<Users> {

    ExistVo exist(String phone);


    Users getUser(Long userId);

    UserVo getInfo(String phone) throws JsonProcessingException;

    String changeName(String userId, String newName) throws JsonProcessingException;

    String uploadImage(MultipartFile file,Long userId);

    WorkInfoListVo workInfo(Long id) throws JsonProcessingException;
}
