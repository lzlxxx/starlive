package com.starlive.org.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlive.org.vo.ExistVo;
import com.starlive.org.vo.UserVo;
import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import com.starlive.org.service.IUsersService;
import com.starlive.org.util.StringUtil;
import com.starlive.org.vo.WorkInfoListVo;
import com.starlive.org.vo.WorkInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author meng
 * @since 2024-10-13
 */
@RestController
@Api(value = "用户接口", tags = "用户模块接口")
@RequestMapping("/user")
public class UsersController {


    @Autowired
    private IUsersService usersService;

    @Value("${cache.ttl}")
    private Long cacheTtl;

    @GetMapping("exist")
    @Operation(summary = "手机号是否存在")
    public WebResult<ExistVo> exist(@RequestParam(value = "tel",required = true) String phone) {
        if(StringUtil.isValidPhoneNumber(phone)) {
            //RequestContextHolder.getRequestAttributes().getAttribute("requestId", RequestAttributes.SCOPE_REQUEST);获取请求id

            ExistVo exist=usersService.exist(phone);
            if(exist.getIsExist()==1) {
                return WebResultUtil.success(exist);
            } else if(exist.getIsExist()==4) {
                return WebResultUtil.failure("500", "服务繁忙，请稍等");
                }
            }
            return WebResultUtil.failure("400","入参不是手机号");
    }

    @GetMapping("info")
    @Operation(summary = "返回用户信息")
    @ApiOperation(value = "返回用户信息", notes = "返回用户信息")
    public WebResult<UserVo> info(@RequestParam(value = "tel",required = true) String phone) throws JsonProcessingException {
       UserVo userVo=usersService.getInfo(phone);
       if(userVo!=null) {
           return WebResultUtil.success(userVo);
       }
       return WebResultUtil.failure("400","用户信息不存在");

    }

    @GetMapping("change/name")
    @Operation(summary = "修改用户名")
    @ApiOperation(value = "修改用户名", notes = "修改用户名")
    public WebResult<Void> changeName(@RequestParam(value = "userId",required = true) String userId,@RequestParam(value = "newName",required = true) String newName)  {
        String code= null;
        try {
            code = usersService.changeName(userId,newName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if(code.equals("200")){
            return WebResultUtil.success();
        }else {
            return WebResultUtil.failure("400","用户不存在");
        }

    }

    @PostMapping("/upload")
    @Operation(summary = "更改用户头像")
    @ApiOperation(value = "更改用户头像", notes = "更改用户头像")
    public WebResult<Map<String,String>> uploadFile(@RequestParam(value = "file",required = true) MultipartFile file,@RequestParam("userId") String userId) {
        Long id=Long.parseLong(userId);
        String url = usersService.uploadImage(file,id);
        if((url.equals("400"))){
            return WebResultUtil.failure("400","图片格式不符合jpg/png");
        }else{
            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            return WebResultUtil.success(response);
        }


    }
    @GetMapping("/info/id")
    @Operation(summary = "个人主页作品信息展示")
    public WebResult<WorkInfoListVo> workInfo(@RequestParam(value = "userId",required = true) String userId) throws JsonProcessingException {
        Long id=Long.parseLong(userId);

        return WebResultUtil.success(usersService.workInfo(id));
    }

}
