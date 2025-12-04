package com.starlive.org.service.imp;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.starlive.org.config.MinioConfig;
import com.starlive.org.config.MinioConfigInfo;
import com.starlive.org.enity.Files;
import com.starlive.org.errcode.BaseErrorCode;
import com.starlive.org.mapper.IFileUploadMapper;
import com.starlive.org.model.FileUploadInfo;
import com.starlive.org.model.UrlVo;
import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import com.starlive.org.service.IFilesService;
import com.starlive.org.util.MinioUtil;
import com.starlive.org.util.RedisUtil;
import com.starlive.org.util.StringUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;


import java.io.File;
import java.time.LocalDateTime;

@Service
@Slf4j
public class FilesServiceImp implements IFilesService {

    @Resource
    private MinioConfigInfo minioConfigInfo;

    @Resource
    private MinioUtil minioUtil;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private IFileUploadMapper fileUploadMapper;

    @Override
    public WebResult initMultipartUpload(FileUploadInfo fileUploadInfo) {
        //1.从缓存中拿出已经初始化过的文件信息
        Object jsonData =  redisUtil.get(fileUploadInfo.getMd5());
        String jsonString = JSON.toJSONString(jsonData);
        FileUploadInfo fileUploadInfoCache = JSONObject.parseObject(jsonString, FileUploadInfo.class);

        String minioFile; //minio上的文件名

        //2.如果缓存有,说明切片文件是断点续传
        if (fileUploadInfoCache != null){
            fileUploadInfo = fileUploadInfoCache;
            //2.1取出文件名
            minioFile = fileUploadInfo.getObject();
        }else {//3.如果缓存中没有,说明文件还未初始化,需要给每个切片分配签名
            //3.1 获取全文件名
            String fullFileName = fileUploadInfo.getFileName();
            //3.2 获取文件后缀
            String suffix = FileUtil.extName(fullFileName);
            //3.3 获取文件名,不带后缀
            String fileName = FileUtil.mainName(fullFileName); //文件名，不带后缀名
            //3.4 生成当前时间戳
            String time = DateUtil.format(LocalDateTime.now(), "yyyy/MM/dd");
            //3.5 生成minio上面的文件名
            minioFile = time + "/" + fileName + "_" + fileUploadInfo.getMd5() + "." + suffix;
            fileUploadInfo.setObject(minioFile);
        }

        UrlVo urlVo = null;

        //4. 如果文件分片数只有一个说明是单文件,直传秒过
        if (fileUploadInfo.getChunkCount() == 1){
            //4.1 单文件直接发放签名
            log.info("当前分片数量{}单文件上传", fileUploadInfo.getChunkCount());
            //4.2 进入发放签名逻辑
            urlVo = minioUtil.getUploadObjectUrl(fileUploadInfo.getContentType(), minioFile);
        }else {//5. 如果文件分片数有多个
            log.info("分片总数量:{}", fileUploadInfo.getChunkCount());
            //5.1 进入发放分片签名逻辑
            urlVo = minioUtil.initMultiPartUpload(fileUploadInfo, minioFile);
        }

        if (urlVo == null){
            return WebResultUtil.failure(BaseErrorCode.REMOTE_ERROR.code(), "分片签名获取失败");
        }

        fileUploadInfo.setUploadId(urlVo.getUploadId());
        //6. 将分片、单文件信息存入redis,存活时间12个小时
        redisUtil.setAndTTL(fileUploadInfo.getMd5(), fileUploadInfo, 60 * 60 * 12);
        return WebResultUtil.success(urlVo);
    }

    @Override
    public WebResult mergeFilePart(String md5) {
        //1. 从redis中获取文件相关信息
        Object jsonData =  redisUtil.get(md5);
        if (jsonData == null){
            return WebResultUtil.failure(BaseErrorCode.REMOTE_ERROR.code(), "请先初始化文件");
        }

        String jsonString = JSON.toJSONString(jsonData);
        FileUploadInfo fileUploadInfoCache = JSONObject.parseObject(jsonString, FileUploadInfo.class);

        //2. 生成文件存放到minio中的路径
        String minioUrl = StrUtil.format("{}/{}/{}", minioConfigInfo.getEndpoint(), minioConfigInfo.getBucket(), fileUploadInfoCache.getObject());
        log.info("文件合并后的地址:{}", minioUrl);

        //3. 获取文件分片数
        Integer chunkCount = fileUploadInfoCache.getChunkCount();

        //4. 如果块数为1 说明是单文件,否则就调用分片合并逻辑
        if (chunkCount == 1 || minioUtil.mergeFile(fileUploadInfoCache.getObject(), fileUploadInfoCache.getUploadId())){
            //5.删除redis中的文件相关信息
            redisUtil.delete(md5);
            Files files = new Files();
            BeanUtils.copyProperties(fileUploadInfoCache, files);
            files.setUrl(minioUrl);
            files.setBucket(minioConfigInfo.getBucket());
            files.setOriginFileName(fileUploadInfoCache.getFileName());
            files.setIsDelete("0");
            //6. 将合并后的信息存入数据库
            fileUploadMapper.insertFile(files);
            return WebResultUtil.success("文件合并成功！");
        }
        return WebResultUtil.failure(BaseErrorCode.REMOTE_ERROR.code(), "文件合并失败");
    }

}
