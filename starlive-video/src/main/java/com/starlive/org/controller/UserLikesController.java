package com.starlive.org.controller;

import com.starlive.org.errcode.BaseErrorCode;
import com.starlive.org.model.LikePage;
import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import com.starlive.org.service.IUserLikesService;
import com.starlive.org.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/video/likes")
@Api(value = "", tags = "")
@Validated
public class UserLikesController {
    @Resource
    private IUserLikesService userLikesService;

    @Resource
    private IUserService userService;

    @GetMapping("/get")
    public WebResult getLikesList(@RequestParam(value = "id") @Min(1) Integer userId, @RequestParam(value = "pageNum") @Min(1) Integer pageNum){
        //1.拿到用户id,查询数据库中是否存在该用户
        if (userService.findById(userId) == null){
            return WebResultUtil.failure(BaseErrorCode.USER_NAME_EXIST_ERROR.code(), "用户不存在");
        }
        //2.如果存在用户就进入业务层
        List<LikePage> like = userLikesService.findLike(userId, pageNum);
        //3. 返回数据
        return like == null ? WebResultUtil.failure(BaseErrorCode.USER_NAME_EXIST_ERROR.code(), "用户喜欢列表触底") :
                              WebResultUtil.success(like);
    }
}
