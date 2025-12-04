package com.starlive.org.util;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.HashMultimap;
import com.starlive.org.config.CustomMinioClient;
import com.starlive.org.config.MinioConfigInfo;
import com.starlive.org.model.FileUploadInfo;
import com.starlive.org.model.UrlVo;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListPartsResponse;
import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import io.minio.http.Method;
import io.minio.messages.Part;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MinioUtil {

    @Resource
    private MinioClient minioClient;
    private CustomMinioClient minioAsyncClient;

    @Resource
    private MinioConfigInfo minioConfigInfo;

    // spring自动注入会失败
    @PostConstruct
    public void init() {
        MinioAsyncClient minioAsyncClient1 = MinioAsyncClient.builder()
                .endpoint(minioConfigInfo.getEndpoint())
                .credentials(minioConfigInfo.getAccessKey(), minioConfigInfo.getSecretKey())
                .build();
        minioAsyncClient = new CustomMinioClient(minioAsyncClient1);
    }

    /**
     * 初始化分片签名
     * @param fileUploadInfo 前端传入的文件信息
     * @param minioFileName minio上的文件名
     * @return UploadUrlsVO
     */
    public UrlVo initMultiPartUpload(FileUploadInfo fileUploadInfo, String minioFileName) {
        //1. 以下是提前取出一些公共资源
        Integer chunkCount = fileUploadInfo.getChunkCount(); //分片数
        String contentType = fileUploadInfo.getContentType(); //请求头中指定的类型
        String uploadId = fileUploadInfo.getUploadId();  //minio分配给我们的uploadId

        log.info("文件<{}> - 分片<{}> 初始化分片上传数据 请求头 {}", minioFileName, chunkCount, contentType);
        UrlVo urlVo = new UrlVo();
        try {
            //2. 如果请求头中没有指定类型，我们需要手动添加一个application/octet-stream
            HashMultimap<String, String> headers = HashMultimap.create();
            if (contentType == null || contentType.equals("")) {
                contentType = "application/octet-stream";
            }
            headers.put("Content-Type", contentType);

            //如果没有分配uploadId
            if (fileUploadInfo.getUploadId() == null || fileUploadInfo.getUploadId().equals("")) {
                uploadId = minioAsyncClient.initMultiPartUpload(minioConfigInfo.getBucket(), null, minioFileName, headers, null);
            }
            urlVo.setUploadId(uploadId);
            List<String> partList = new ArrayList<>();
            Map<String, String> reqParams = new HashMap<>();
            reqParams.put("uploadId", uploadId);
            log.info("uploadId : {}", uploadId);
            //循环生成签名地址
            for (int i = 1; i <= chunkCount; i++) {
                reqParams.put("partNumber", String.valueOf(i));
                String uploadUrl = minioAsyncClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(minioConfigInfo.getBucket())
                        .object(minioFileName)
                        .extraHeaders(headers)
                        .extraQueryParams(reqParams)
                        .expiry(minioConfigInfo.getExpiry(), TimeUnit.DAYS)
                        .build());
                partList.add(uploadUrl);
            }
            urlVo.setUrls(partList);
            log.info("文件分片成功");
            return urlVo;
        }catch (Exception e){
            e.printStackTrace();
            log.info("文件分片失败");
            return null;
        }
    }

    /**
     * 单文件直签上传
     * @param contentType
     * @param object
     * @return
     */
    public UrlVo getUploadObjectUrl(String contentType, String object) {
        try {
            log.info("<{}> 开始单文件上传<minio>", object);
            UrlVo urlsVO = new UrlVo();
            List<String> urlList = new ArrayList<>();
            //1. 主要是针对图片，若需要通过浏览器直接查看，而不是下载，需要指定对应的 content-type
            HashMultimap<String, String> headers = HashMultimap.create();
            if (contentType == null || contentType.equals("")) {
                contentType = "application/octet-stream";
            }
            headers.put("Content-Type", contentType);

            //2. 单文件不涉及minio系统分配的uploadId 所以我们随机生成一个，保证不重名就行
            String uploadId = IdUtil.simpleUUID();
            Map<String, String> reqParams = new HashMap<>();
            reqParams.put("uploadId", uploadId);

            //3. 调用api minio返回一个签名，前端拿这个签名就能上传文件
            String url = minioAsyncClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(minioConfigInfo.getBucket())
                    .object(object)
                    .extraHeaders(headers)
                    .extraQueryParams(reqParams)
                    .expiry(minioConfigInfo.getExpiry(), TimeUnit.DAYS)
                    .build());
            //4. 将签名添加到签名列表当中，因为是单文件，所以列表当中只有一个
            urlList.add(url);
            //5. 将随机生成uploadId添加到返回体当中
            urlsVO.setUploadId(uploadId);
            //6. 将签名列表添加到返回体当中
            urlsVO.setUrls(urlList);
            return urlsVO;
        }catch (Exception e){
            //7. 触发异常
            log.error("单文件上传失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 合并分片
     * @param fileName 文件名
     * @param uploadId uploadId
     * @return true成功 false失败
     */
    public boolean mergeFile(String fileName, String uploadId){

        try {
            //1. 获取所有分片
            List<Part> partsList = getParts(fileName, uploadId);

            Part[] parts = new Part[partsList.size()];
            int partNumber = 1;
            for (Part part : partsList) {
                //2. minio系统中每个分片都有片号和etag标签,将这两个关键信息存入Part数组中
                parts[partNumber - 1] = new Part(partNumber, part.etag());
                partNumber++;
            }
            //3. 将Part数组和uploadId发送进合并分片api 即可合并
            minioAsyncClient.mergeMultipartUpload(minioConfigInfo.getBucket(), null, fileName, uploadId, parts, null, null);
            return true;
        }catch (Exception e){
            log.info("文件分片合并失败 mergeFile");
            e.printStackTrace();
            return false;
        }
    }

    private  List<Part> getParts(String object, String uploadId) throws Exception {

        int partNumberMarker = 0; //minio当中页码号
        boolean isTruncated = true;
        List<Part> parts = new ArrayList<>(); //存放part list
        while(isTruncated){
            //1. 获取当前页的分片
            ListPartsResponse partResult = minioAsyncClient.listMultipart(minioConfigInfo.getBucket(), null, object, 1000, partNumberMarker, uploadId, null, null);
            parts.addAll(partResult.result().partList());
            //2. 检查是否还有更多分片
            isTruncated = partResult.result().isTruncated();
            if (isTruncated) {
                //3. 更新partNumberMarker以获取下一页的分片数据
                partNumberMarker = partResult.result().nextPartNumberMarker();
            }
        }
        return parts;
    }



}
