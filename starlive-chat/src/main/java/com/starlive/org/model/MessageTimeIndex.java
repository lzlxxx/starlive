package com.starlive.org.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Data
public class MessageTimeIndex {
    // 使用 ConcurrentSkipListMap 保证线程安全和高效的范围查询
    private NavigableMap<LocalDateTime, String> timeToFileMap = new ConcurrentSkipListMap<>();
    private NavigableMap<LocalDateTime, Long> timeToPositionMap = new ConcurrentSkipListMap<>();
    
    // 每个文件的消息数量
    private NavigableMap<String, Integer> fileMessageCount = new ConcurrentSkipListMap<>();
    
    // 添加分片文件的时间范围索引，避免遍历所有分片
    @Data
    public static class FileTimeRange {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String fileName;
    }
    
    // 文件时间范围索引
    private NavigableMap<LocalDateTime, FileTimeRange> fileTimeRanges = new ConcurrentSkipListMap<>();
    
    /**
     * 更新文件时间范围
     */
    public void updateFileTimeRange(String fileName, LocalDateTime messageTime) {
        FileTimeRange range = fileTimeRanges.get(messageTime);
        if (range == null) {
            range = new FileTimeRange();
            range.setFileName(fileName);
            range.setStartTime(messageTime);
            range.setEndTime(messageTime);
            fileTimeRanges.put(messageTime, range);
        } else {
            range.setEndTime(messageTime);
        }
    }
    
    /**
     * 获取指定时间范围内的文件列表
     */
    public NavigableMap<LocalDateTime, FileTimeRange> getFileRangesInTimeRange(
            LocalDateTime startTime, LocalDateTime endTime) {
        return fileTimeRanges.subMap(startTime, true, endTime, true);
    }
} 