package com.starlive.org.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.starlive.org.config.MinioConfig;
import com.starlive.org.model.ChatMessage;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Configuration
public class MinioServiceImpl {
@Autowired
private MinioConfig minioConfig;
@Autowired
private MinioClient minioClient;
@Value("${minio.bucketName}")
private String bucketName;

/**
 * 上传图片到 MinIO
 *
 * @param storagePath MinIO 存储路径（按用户/群聊分类）
 * @param base64Image Base64 编码的图片数据
 * @return 是否上传成功
 */
public boolean uploadImageToMinio(String storagePath, String base64Image) {
    try {
        // **解码 Base64 转换为字节流**
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

        // **上传到 MinIO**
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(storagePath)
                        .stream(inputStream, imageBytes.length, -1)  // -1 让 MinIO 自动计算大小
                        .contentType("image/jpeg")  // 你可以根据实际情况调整
                        .build()
        );
        log.info(" 图片上传成功: {}", storagePath);
        return true;
    } catch (MinioException e) {
        log.info(" 图片上传失败: {}", storagePath);
        log.error("❌ MinIO 上传失败: {}", e.getMessage(), e);
    } catch (Exception e) {
        log.error("❌ 图片上传异常: {}", e.getMessage(), e);
    }
    return false;
}

public String generatePrivateChatImagePath(String senderId, String receiverId, String timestamp) {
    // 按字典序排序
    List<String> sortedIds = Arrays.asList(senderId, receiverId);
    Collections.sort(sortedIds);
    //精确到天
    Date parsedDate = DateUtil.parse(timestamp, "yyyy-MM-dd HH:mm:ss");
    String formattedDate = DateUtil.format(parsedDate, "yyyy-MM-dd");

    //生成文件夹路径（按用户ID排序）
    String chatFolder = sortedIds.get(0) + "_" + sortedIds.get(1);
    // 生成唯一文件名
    String uniqueFileName =IdUtil.simpleUUID();

    // **返回最终存储路径（包括时间和用户路径）**
    return "private_chats/" + chatFolder + "/" + formattedDate + "/" + uniqueFileName + ".jpg";
}

public String generateActivityChatImagePath(String activityId, String timestamp) {

    //精确到天
    Date parsedDate = DateUtil.parse(timestamp, "yyyy-MM-dd HH:mm:ss");
    String formattedDate = DateUtil.format(parsedDate, "yyyy-MM-dd");

    // 生成唯一文件名
    String uniqueFileName = IdUtil.simpleUUID();

    // **返回最终存储路径（包括时间和用户路径）**
    return "activity_chats/" + activityId + "/" + formattedDate + "/" + uniqueFileName + ".jpg";
}

public void sendMessageWithImage(String storagePath, String base64Image) {
    // 异步上传图片，并实现重试机制
    CompletableFuture.runAsync(() -> {
        // 最大重试次数
        int maxRetries = 3;
        // 重试间隔，单位：秒
        long retryInterval = 2;

        boolean success = false;
        int attempt = 0;

        while (attempt < maxRetries && !success) {
            try {
                // 调用上传图片方法
                success = uploadImageToMinio(storagePath, base64Image);

                if (!success) {
                    attempt++;
                    log.error("Image upload failed on attempt {}: {}", attempt, storagePath);
                    // 等待指定的时间后重试
                    TimeUnit.SECONDS.sleep(retryInterval);
                }
            } catch (Exception e) {
                log.error("Error uploading image on attempt {}: {}", attempt, e.getMessage());
                attempt++;
                try {
                    // 等待一段时间后重试
                    TimeUnit.SECONDS.sleep(retryInterval);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (!success) {
            log.error("Image upload failed after {} attempts: {}", maxRetries, storagePath);
        }
    });
}
public void handleImageMessage(ChatMessage message, boolean isGroupChat) {
    if (message.getImageData() != null) {
        // 生成存储路径
        String uniqueFileName = isGroupChat
                ? generateActivityChatImagePath(message.getActivityId(), message.getTimestamp())
                : generatePrivateChatImagePath(message.getFromUserId(), message.getToUserId(), message.getTimestamp());

        String imageData = message.getImageData();
        // 生成图片 URL
        String imageUrl = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + uniqueFileName;
        message.setImageUrl(imageUrl);
        message.setImageData(null);

        // 异步上传图片
        CompletableFuture.runAsync(() -> {
            boolean success = uploadImageToMinio(uniqueFileName, imageData);
            if (!success) {
                sendMessageWithImage(uniqueFileName, imageData);
            }
        });
    }
}
}
