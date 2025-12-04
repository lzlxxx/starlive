package com.starlive.org.service.impl;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlive.org.mapper.VideoMapper;
import com.starlive.org.util.RedisUtil;
import com.starlive.org.vo.ExistVo;
import com.starlive.org.vo.UserVo;
import com.starlive.org.entity.Followers;
import com.starlive.org.entity.GeoPoint;
import com.starlive.org.entity.Users;
import com.starlive.org.mapper.FollowersMapper;
import com.starlive.org.mapper.UsersMapper;
import com.starlive.org.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starlive.org.vo.WorkInfoListVo;
import com.starlive.org.vo.WorkInfoVo;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author meng
 * @since 2024-10-11
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

    public static final Random random=new Random();
    private static final int Time=60;

    @Autowired
    private FollowersMapper followersMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Override
    public ExistVo exist(String phone) {

        //0：用户不存在，1：用户存在，4：服务繁忙，请稍等
        int phoneExist = 0;

        String cacheKey = "user:" + phone;
        //1.从缓存获取手机号是否存在
        Integer cacheValue = (Integer) redisUtil.get(cacheKey);
        if (cacheValue != null && cacheValue == 1) {
            return new ExistVo(cacheValue);
        }else {
            //  2.防止缓存击穿，尝试获取分布式锁
            String lockKey = "lock:" + phone;
            String lockValue = UUID.randomUUID().toString();
            Boolean isLockAcquired = redisUtil.setIfAbsent(lockKey,lockValue,10,TimeUnit.MINUTES); // 10秒锁定

            try {
                if (Boolean.TRUE.equals(isLockAcquired)) {
                    // 3.再次检查缓存，双重检查，防止多个线程等待锁之后重复查询数据库
                    cacheValue = (Integer) redisUtil.get(cacheKey);
                    if (cacheValue != null && cacheValue != 0) {
                        return new ExistVo(cacheValue);
                    }
                    //4.从数据库查询
                    Users u = baseMapper.selectByPhone(phone);
                    if (u != null) {
                        phoneExist = 1;
                        //  5.将结果写入缓存，并设置缓存时间
                        redisUtil.set(cacheKey, phoneExist, 60, TimeUnit.MINUTES);
                    }else {
                        //6.用户不存在，创建用户
                        Users user = new Users();
                        user.setPhone(phone);
                        String name="用户"+random.nextInt(1000);
                        user.setUsername(name);
                        double longitude = 116.4074; // 示例经度
                        double latitude = 39.9042;    // 示例纬度

                        GeoPoint geoPoint = new GeoPoint(longitude, latitude);
                        user.setLocation(geoPoint);
                        baseMapper.addUser(user);
                        u=baseMapper.selectByPhone(phone);
                        Followers followers = new Followers();
                        followers.setFollowerId(u.getUserId());
                        followers.setFollowedId(u.getUserId());
                        followersMapper.insertFollowers(followers);
                        phoneExist = 1;
                        redisUtil.set(cacheKey, phoneExist, Time, TimeUnit.MINUTES);
                    }
                    return new ExistVo(phoneExist);
                } else {
                    //  7.如果获取锁失败，短暂休眠后重试从缓存获取
                    for (int i = 0; i < 5; i++) {
                        Thread.sleep(100);
                        cacheValue = (Integer) redisUtil.get(cacheKey);
                        if (cacheValue != null && cacheValue == 1) {
                            return new ExistVo(cacheValue);
                        }
                    }
                    return new ExistVo(4);
                }
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                //8.释放锁，使用 Lua 脚本保证原子性
                // Lua 脚本
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) " +
                        "else return 0 end";

                // 执行 Lua 脚本
                redisUtil.executeLuaScript(luaScript,lockKey,lockValue);
            }
            return new ExistVo(phoneExist);
        }
    }

    @Override
    public Users getUser(Long userId) {
        return baseMapper.getUser(userId);
    }

    @Override
    public UserVo getInfo(String phone) throws JsonProcessingException {

        String cacheKey = "userinfo:" + phone;
        ObjectMapper objectMapper = new ObjectMapper();
        UserVo userVo=null;
        //1.从缓存获取
        String userVoJson = (String) redisUtil.get(cacheKey);
        if(userVoJson != null) {
            userVo = objectMapper.readValue(userVoJson, UserVo.class);
            if (userVo != null) {
                return userVo;
            }
        }else {
            //  2.防止缓存击穿，尝试获取分布式锁
            String lockKey = "lockinfo:" + phone;
            String lockValue=UUID.randomUUID().toString();
            try {
                Boolean isLockAcquired = redisUtil.setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(isLockAcquired)) {
                    userVoJson= (String) redisUtil.get(cacheKey);
                    if(userVoJson != null) {
                        userVo = objectMapper.readValue(userVoJson, UserVo.class);
                        if (userVo != null) {
                            return userVo;
                        }
                    }

                    //3.从数据库获取
                    Users u=baseMapper.selectByPhone(phone);
                    if(u!=null) {
                        Long id=u.getUserId();
                        Followers followers=followersMapper.selectFollowers(id);
                        if(followers != null) {
                            userVo = new UserVo();
                            userVo.setUserName(u.getUsername());
                            userVo.setUserId(u.getUserId());
                            userVo.setFollowedId(followers.getFollowerId());
                            userVo.setBio(u.getBio());
                            userVoJson = objectMapper.writeValueAsString(userVo);
                            redisUtil.set(cacheKey, userVoJson, Time, TimeUnit.MINUTES);
                        }
                        else return null;

                    }

                }else {
                    for(int i=0;i<5;i++){
                        Thread.sleep(100);
                        userVoJson= (String) redisUtil.get(cacheKey);
                        if(userVoJson == null) {
                            return null;
                        }
                        userVo=objectMapper.readValue(userVoJson, UserVo.class);
                        if (userVo != null) {
                            return userVo;
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } finally {
                // Lua 脚本
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) " +
                        "else return 0 end";
                // 执行 Lua 脚本
                redisUtil.executeLuaScript(luaScript,lockKey,lockValue);
            }
        }
        return userVo;
    }

    @Override
    public String changeName(String userId, String newName) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Long id = Long.parseLong(userId);

        // 1.查询用户
        Users user = baseMapper.selectByUserId(id);
        if (user == null) {
            return "400"; // 用户不存在，直接返回
        }

        // 2.更新用户名
        baseMapper.updateUserName(newName, id);

        // 通过新名字查询用户
        user = baseMapper.selectByUserId(id);
        if (user == null) {
            return "400"; // 更新失败或用户未找到，直接返回
        }

        // 查询关注者
        Followers followers = followersMapper.selectFollowers(id);
        UserVo userVo = new UserVo();
        if (followers == null) {
            userVo.setFollowedId(0L); ; // 没有找到关注信息，返回0ID
        }

        // 构造 UserVo 并缓存到 Redis

        userVo.setFollowedId(followers.getFollowerId());
        userVo.setBio(user.getBio());
        userVo.setUserName(user.getUsername());
        userVo.setUserId(user.getUserId());

        String cacheKey = "userinfo:" + user.getPhone();
        String userVoJson = objectMapper.writeValueAsString(userVo);
        redisUtil.set(cacheKey, userVoJson, Time, TimeUnit.MINUTES);

        return "200";
    }




    @Override
    public String uploadImage(MultipartFile file,Long userId) {
        try {

            if("image/png".equals(file.getContentType())||"image/jpeg".equals(file.getContentType())||"image/jpg".equals(file.getContentType())){
                // 1.生成唯一文件名
                String dateDir = DateUtil.format(new Date(), "yyyyMMdd");
                String uFileName = dateDir+"/"+UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();

                // 2.获取文件流和文件大小
                InputStream inputStream = file.getInputStream();
                long fileSize = file.getSize();

                // 3.上传文件到 MinIO
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(uFileName)
                                .stream(inputStream, fileSize, -1)
                                .contentType(file.getContentType()) // 设置文件内容类型
                                .build()
                );

                String url=getObjectURL(bucketName,uFileName);
                baseMapper.updateUserUrl(userId,url);
                // 返回文件的 URL
                return url;
            }else {
                return "400";
            }

        } catch (MinioException e) {
            throw new RuntimeException("上传文件到 MinIO 失败: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败", e);
        }
    }



    public String getObjectURL(String bucketName, String objectName) throws Exception{
        String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .build());
        System.out.println(url);
        return url;
    }

    @Override
    public WorkInfoListVo workInfo(Long id) throws JsonProcessingException {
        String cacheKey = "workInfo:" + id;
        ObjectMapper objectMapper = new ObjectMapper();
        String workInfoJson = (String) redisUtil.get(cacheKey);
        if (workInfoJson == null) {
            String lockKey = "workInfo:" + id;
            String lockValue =UUID.randomUUID().toString();
            Boolean isLockAcquired = redisUtil.setIfAbsent(lockKey, lockValue, 5, TimeUnit.SECONDS);
            try{
                //1.防止缓存击穿，尝试获取分布式锁
                if(Boolean.TRUE.equals(isLockAcquired)) {
                    WorkInfoListVo workInfoListVo = new WorkInfoListVo();
                    workInfoListVo.setWorknum(videoMapper.getVideoCount(id));
                    workInfoListVo.setWorkInfoList(videoMapper.getWorkInfo(id));
                    workInfoJson=objectMapper.writeValueAsString(workInfoListVo);
                    //2.设置60秒过期，保证数据一致
                    redisUtil.set(cacheKey, workInfoJson, Time, TimeUnit.SECONDS);
                    return workInfoListVo;

                }else {
                    for(int i=0;i<5;i++){
                        Thread.sleep(100);
                        workInfoJson=(String) redisUtil.get(cacheKey);
                        if (workInfoJson != null) {
                            return objectMapper.readValue(workInfoJson, WorkInfoListVo.class);
                        }
                        return null;
                    }
                }
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } finally {
                // Lua 脚本
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) " +
                        "else return 0 end";
                // 执行 Lua 脚本
                redisUtil.executeLuaScript(luaScript,lockKey,lockValue);
            }
        }

        return objectMapper.readValue(workInfoJson, WorkInfoListVo.class);
    }

}
