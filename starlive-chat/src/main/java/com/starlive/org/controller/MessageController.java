package com.starlive.org.controller;

import com.starlive.org.exception.AbstractException;
import com.starlive.org.model.ChatMessageVo;

import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import com.starlive.org.service.impl.MessageFileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/messages/history")
public class MessageController {

    @Autowired
    private MessageFileStorage messageFileStorage;
    /**
     * 获取与指定用户的私聊记录
     */
    @GetMapping("/private/{userId}/{targetUserId}")
    public WebResult getPrivateMessages(
            @PathVariable String userId,
            @PathVariable String targetUserId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page) {
        try {
            // 去除首尾空格后解析日期
            LocalDate start = LocalDate.parse(startDate.trim());
            LocalDate end = LocalDate.parse(endDate.trim());

            LocalDateTime startTime = start.atStartOfDay();
            LocalDateTime endTime = end.atTime(23, 59, 59);

          ChatMessageVo messages = messageFileStorage.findPrivateMessagesByTimeRange(
                userId, targetUserId, startTime, endTime, page);
            return WebResultUtil.success(messages);
        } catch (Exception e) {
            log.error("Error getting private messages", e);
            return WebResultUtil.failure((AbstractException) e);
        }
    }

    /**
     * 获取指定活动房间的聊天记录
     */
    @GetMapping("/room/{roomId}")
    public WebResult getRoomMessages(
            @PathVariable String roomId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page) {
        try {
            // 去除首尾空格后解析日期
            LocalDate start = LocalDate.parse(startDate.trim());
            LocalDate end = LocalDate.parse(endDate.trim());

            LocalDateTime startTime = start.atStartOfDay();
            LocalDateTime endTime = end.atTime(23, 59, 59);

            ChatMessageVo messages = messageFileStorage.findActivityMessagesByTimeRange(
                roomId, startTime, endTime, page);
            return WebResultUtil.success(messages);
        } catch (Exception e) {
            log.error("Error getting room messages", e);
            return WebResultUtil.failure((AbstractException) e);
        }
    }
}