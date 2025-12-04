package com.starlive.org.service.impl;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.starlive.org.config.MessageStorageConfig;
import com.starlive.org.model.ChatMessage;
import com.starlive.org.model.ChatMessageVo;
import com.starlive.org.model.MessageTimeIndex;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Service
public class MessageFileStorage {

    @Value("${chat.storage.path}")
    private String baseStoragePath;

    @Autowired
    private MessageStorageConfig config;


    private static final String PRIVATE_CHAT_DIR = "private";

    private static final String ACTIVITY_CHAT_DIR = "activity";
    private static final String FILE_SUFFIX = ".txt";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ObjectMapper objectMapper;

    // 文件索引管理
    private final ConcurrentMap<String, MessageFileIndex> fileIndices = new ConcurrentHashMap<>();

    @Data
    private static class MessageFileIndex {
        private int currentIndex = 0;
        private long currentSize = 0;
        private int messageCount = 0;
    }

    // 消息缓存配置
    private static final int CACHE_MAX_SIZE = 1000;  // 最大缓存消息数
    private static final int CACHE_EXPIRE_MINUTES = 5;  // 缓存过期时间

    // 消息缓存，key为文件名，value为消息列表
    private final Cache<String, List<ChatMessage>> messageCache = Caffeine.newBuilder()
            .maximumSize(CACHE_MAX_SIZE)
            .expireAfterWrite(CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .recordStats()  // 记录缓存统计信息
            .build(this::loadMessagesFromFile);

    // 批量消息处理
    private static final int BATCH_SIZE = 100;
    private final Map<String, List<ChatMessage>> messageBuffer = new ConcurrentHashMap<>();

    // 时间索引缓存
    private final Map<String, MessageTimeIndex> timeIndices = new ConcurrentHashMap<>();

    // 分页大小
    private static final int PAGE_SIZE = 20;

    // 存储时使用的时间格式（精确到秒）
    private static final String STORAGE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter STORAGE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(STORAGE_TIME_PATTERN);

    // 查询时使用的时间格式（精确到日）
    private static final String QUERY_TIME_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter QUERY_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(QUERY_TIME_PATTERN);
    public MessageFileStorage() {
        this.objectMapper = new ObjectMapper();

    }

    @PostConstruct
    public void init() throws IOException {
        // 创建存储目录
        createDirectories(
            Paths.get(baseStoragePath, PRIVATE_CHAT_DIR),
            Paths.get(baseStoragePath, ACTIVITY_CHAT_DIR)
        );
        // 配置 ObjectMapper jdk8不支持Jackson的LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 设置字符编码
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        // 允许特殊字符
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        // 允许单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许不带引号的字段名
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许注释
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        // 允许尾部逗号
        objectMapper.configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true);
        // 允许反斜杠转义
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        // 允许数字开头的字段名
        objectMapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        // 强制所有字段名使用双引号
        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
    }

    private void createDirectories(Path... paths) throws IOException {
        for (Path path : paths) {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created directory: {}", path);
            }
        }
    }

    // 存储私聊消息
    public void savePrivateMessage(ChatMessage message) {
        String fileName = getPrivateMessageFileName(message.getFromUserId(), message.getToUserId());
        saveMessage(fileName, message);
    }

    // 获取私聊历史消息
    public List<ChatMessage> getPrivateMessages(String fromUserId, String toUserId, int limit) {
        String fileName = getPrivateMessageFileName(fromUserId, toUserId);
        return getMessages(fileName, limit);
    }

//    // 存储房间消息
//    public void saveRoomMessage(ChatMessage message) {
//        String fileName = getRoomMessageFileName(message.getRoomId());
//        saveMessage(fileName, message);
//    }

//
//    // 获取房间历史消息
//    public List<ChatMessage> getRoomMessages(String roomId, int limit) {
//        String fileName = getRoomMessageFileName(roomId);
//        return getMessages(fileName, limit);
//    }
    //存储活动群聊信息
    public void saveActivityMessage(ChatMessage message) {
        if (message.getActivityId() == null) {
            log.warn("Invalid activity message: missing activity ID");
            return;
        }
        String fileName = getActivityMessageFileName(message.getActivityId());
        saveMessage(fileName, message);
        log.debug("Activity message saved: activityId={}", message.getActivityId());
    }

    private String getActivityMessageFileName(String activityId) {
        return activityId ;
    }

    private void saveMessage(String fileName, ChatMessage message) {
        lock.writeLock().lock();
        try {
            // 确定消息类型并获取基础目录
            String baseDir;
            if (message.getActivityId() != null) {
                baseDir = ACTIVITY_CHAT_DIR;
            } else {
                baseDir = PRIVATE_CHAT_DIR;
            }

            // 创建聊天目录
            Path chatDir = Paths.get(baseStoragePath, baseDir, fileName);
            if (!Files.exists(chatDir)) {
                Files.createDirectories(chatDir);
                log.info("Created chat directory: {}", chatDir);
            }

            // 获取当前消息文件
            MessageFileIndex index = fileIndices.computeIfAbsent(fileName, k -> new MessageFileIndex());
            Path messageFile = chatDir.resolve(String.format("messages_%d%s", index.getCurrentIndex(), FILE_SUFFIX));

            // 格式化时间戳（精确到秒）
            if (message.getTimestamp() != null) {
                LocalDateTime dateTime = LocalDateTime.parse(message.getTimestamp(),DATE_TIME_FORMATTER);
                String formattedTime = dateTime.format(STORAGE_TIME_FORMATTER);
                message.setTimestamp(formattedTime);
            } else {
                String currentTime = LocalDateTime.now().format(STORAGE_TIME_FORMATTER);
                message.setTimestamp(currentTime);
            }

            // 写入消息
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(messageFile.toFile(), true),
                            StandardCharsets.UTF_8))) {
                // 确保生成的 JSON 格式正确
                String messageJson = objectMapper.writeValueAsString(message);
                log.debug("Writing message: {}", messageJson);

                // 确保 JSON 格式正确
                if (!messageJson.startsWith("{\"")) {
                    messageJson = "{\"" + messageJson.substring(1);
                }
                writer.write(messageJson);
                writer.newLine();
                writer.flush();

                // 更新时间索引
                updateTimeIndex(fileName, message, Files.size(messageFile) -
                        (messageJson.getBytes(StandardCharsets.UTF_8).length +
                                System.lineSeparator().getBytes(StandardCharsets.UTF_8).length));

                log.info("Saved message to file: {}", messageFile);
            }

            // 更新缓存
            messageCache.invalidate(fileName);

        } catch (IOException e) {
            log.error("Error saving message to file: {}", fileName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private List<ChatMessage> getMessages(String fileName, int limit) {
        List<ChatMessage> messages = messageCache.get(fileName, this::loadMessagesFromFile);
        int start = Math.max(0, messages.size() - limit);
        return messages.subList(start, messages.size());
    }

    private List<ChatMessage> loadMessagesFromFile(String fileName) {
        List<ChatMessage> messages = new ArrayList<>();
        Path filePath = Paths.get(baseStoragePath, fileName);
        if (Files.exists(filePath)) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        try {
                            ChatMessage message = objectMapper.readValue(line, ChatMessage.class);
                            messages.add(message);
                        } catch (Exception e) {
                            log.error("Error parsing message line: {} -> {}", line, e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Error loading messages from file: {}", fileName, e);
            }
        }
        return messages;
    }



    private String getPrivateMessageFileName(String fromUserId, String toUserId) {
        if (fromUserId == null || toUserId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 确保用户ID的顺序一致性
        String[] userIds = new String[]{fromUserId, toUserId};
        // 在排序前检查数组元素
        if (userIds[0] != null && userIds[1] != null) {
            Arrays.sort(userIds);
        } else {
            log.error("Invalid user IDs: fromUserId={}, toUserId={}", fromUserId, toUserId);
            throw new IllegalArgumentException("Invalid user IDs");
        }

        // 构建文件路径
        Path chatDir = Paths.get(userIds[0]+"_"+userIds[1]);
        String fileName = String.format("messages_%s_%s.txt", userIds[0], userIds[1]);
        return chatDir.resolve(fileName).toString();
    }

//    private String getRoomMessageFileName(String roomId) {
//        // 只返回房间ID，不包含文件后缀
//        return roomId;
//    }

    /**
     * 获取当前可写入的文件
     */
    private Path getCurrentMessageFile(String baseFileName) {
        lock.readLock().lock();
        try {
            MessageFileIndex index = fileIndices.computeIfAbsent(baseFileName,
                    k -> new MessageFileIndex());

            // 确定基础目录
            String baseDir;
            if (baseFileName.contains("_")) {
                baseDir = PRIVATE_CHAT_DIR;
            } else {
                baseDir = ACTIVITY_CHAT_DIR;
            }
            // 构建完整路径：baseStoragePath/baseDir/chatDir/file
            Path chatDir = Paths.get(baseStoragePath, baseDir, baseFileName);

            // 确保目录存在
            if (!Files.exists(chatDir)) {
                Files.createDirectories(chatDir);
            }

            // 构建文件路径
            Path currentFile = chatDir.resolve(
                    String.format("messages_%d%s", index.getCurrentIndex(), FILE_SUFFIX));

            if (Files.exists(currentFile) &&
                    Files.size(currentFile) >= config.getMaxFileSize()) {
                // 需要创建新文件
                lock.readLock().unlock();
                lock.writeLock().lock();
                try {
                    // 双重检查
                    if (Files.size(currentFile) >= config.getMaxFileSize()) {
                        index.setCurrentIndex(index.getCurrentIndex() + 1);
                        currentFile = chatDir.resolve(
                                String.format("messages_%d%s", index.getCurrentIndex(), FILE_SUFFIX));
                    }
                } finally {
                    lock.writeLock().unlock();
                    lock.readLock().lock();
                }
            }
            return currentFile;
        } catch (IOException e) {
            log.error("Error getting current message file: {}", baseFileName, e);
            return Paths.get(baseStoragePath, "error.txt");
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 清理过期消息
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    public void cleanupOldMessages() {
        try {
            long cutoffTime = System.currentTimeMillis() -
                    (config.getRetentionDays() * 24 * 60 * 60 * 1000L);

            Files.walk(Paths.get(baseStoragePath))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(FILE_SUFFIX))
                    .forEach(p -> {
                        try {
                            if (Files.getLastModifiedTime(p).toMillis() < cutoffTime) {
                                Files.delete(p);
                                log.info("Deleted old message file: {}", p);
                            }
                        } catch (IOException e) {
                            log.error("Error deleting old message file: {}", p, e);
                        }
                    });
        } catch (IOException e) {
            log.error("Error cleaning up old messages", e);
        }
    }


    // 添加缓存统计方法
    public Map<String, Object> getCacheStats() {
        com.github.benmanes.caffeine.cache.stats.CacheStats stats = messageCache.stats();
        Map<String, Object> statsMap = new HashMap<>();
        statsMap.put("hitCount", stats.hitCount());
        statsMap.put("missCount", stats.missCount());
        statsMap.put("evictionCount", stats.evictionCount());
        statsMap.put("averageLoadPenalty", stats.averageLoadPenalty());
        return statsMap;
    }

    @Scheduled(fixedDelay = 10000) // 每十秒执行一次
    public void flushMessageBuffer() {
        messageBuffer.forEach((fileName, messages) -> {
            if (messages.size() >= BATCH_SIZE) {
                flushMessages(fileName, messages);
            }
        });
    }

    private void flushMessages(String fileName, List<ChatMessage> messages) {
        lock.writeLock().lock();
        try {
            Path messageFile = getCurrentMessageFile(fileName);
            try (BufferedWriter writer = Files.newBufferedWriter(messageFile,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                for (ChatMessage message : messages) {
                    String messageJson = objectMapper.writeValueAsString(message) + "\n";
                    writer.write(messageJson);
                }
                writer.flush();
            }

            // 清空缓冲区
            messages.clear();
            // 更新缓存
            messageCache.invalidate(fileName);

        } catch (IOException e) {
            log.error("Error flushing messages to file: {}", fileName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }
// 添加消息搜索功能
public List<ChatMessage> searchMessages(String userId, String keyword) {
    List<ChatMessage> results = new ArrayList<>();
    try {
        Files.walk(Paths.get(baseStoragePath))
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().contains(userId))
                .forEach(p -> {
                    try {
                        List<String> lines = Files.readAllLines(p);
                        for (String line : lines) {
                            ChatMessage message = objectMapper.readValue(line, ChatMessage.class);
                            if (message.getContent().contains(keyword)) {
                                results.add(message);
                            }
                        }
                    } catch (IOException e) {
                        log.error("Error searching messages in file: {}", p, e);
                    }
                });
    } catch (IOException e) {
        log.error("Error searching messages", e);
    }
    return results;
}
/**
 * 构建时间索引
 */
private void buildTimeIndex(String filePath) {
    Path path = Paths.get(filePath);
    String indexKey = path.toString(); // 使用完整路径作为索引键
    MessageTimeIndex index = new MessageTimeIndex();
    long position = 0;

    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
        String line;
        while ((line = reader.readLine()) != null) {
            // 跳过空行
            if (line.trim().isEmpty()) {
                position += line.length() + System.lineSeparator().length();
                continue;
            }

            try {
                // 修复 JSON 格式
                String jsonLine = line.trim();
                if (!jsonLine.startsWith("{")) {
                    if (jsonLine.contains("messageId")) {
                        int start = jsonLine.indexOf("messageId");
                        jsonLine = "{\"" + jsonLine.substring(start);
                    }
                }
                ChatMessage message = objectMapper.readValue(jsonLine, ChatMessage.class);
                LocalDateTime messageTime = LocalDateTime.parse(message.getTimestamp(), STORAGE_TIME_FORMATTER);

                // 更新时间索引
                index.getTimeToPositionMap().put(messageTime, position);
                log.debug("Indexed message: {} at position: {} in file: {}",
                        message.getMessageId(), position, indexKey);

                // 更新文件时间范围
                index.updateFileTimeRange(indexKey, messageTime);

            } catch (Exception e) {
                log.error("Error processing message at position {} in file {}: {}",
                        position, indexKey, e.getMessage());
            }

            position += line.length() + System.lineSeparator().length();
        }

        // 将构建好的索引存入 timeIndices
        timeIndices.put(indexKey, index);
        log.info("Built time index for file: {}, total messages: {}",
                indexKey, index.getTimeToPositionMap().size());

    } catch (IOException e) {
        log.error("Error building time index for file: {}", filePath, e);
    }
}
private String readMessage(RandomAccessFile raf) throws IOException {
    String line = raf.readLine();
    if (line == null || line.trim().isEmpty()) {
        return null;
    }

    // 确保JSON格式完整
    try {
        // 修复可能的JSON格式问题
        String jsonLine = line.trim();
        if (!jsonLine.startsWith("{")) {
            // 如果不是以{开头，尝试找到消息的开始位置
            int start = jsonLine.indexOf("{");
            if (start >= 0) {
                jsonLine = jsonLine.substring(start);
            } else {
                log.warn("Invalid message format, missing opening brace: {}", jsonLine);
                return null;
            }
        }

        // 确保JSON以}结尾
        if (!jsonLine.endsWith("}")) {
            int end = jsonLine.lastIndexOf("}");
            if (end >= 0) {
                jsonLine = jsonLine.substring(0, end + 1);
            } else {
                log.warn("Invalid message format, missing closing brace: {}", jsonLine);
                return null;
            }
        }

        // 验证是否是有效的JSON
        objectMapper.readTree(jsonLine);
        return jsonLine;
    } catch (Exception e) {
        log.error("Error parsing message line: {}", line, e);
        return null;
    }
}
/**
 * 更新时间索引
 */
private void updateTimeIndex(String filePath, ChatMessage message, long position) {
    MessageTimeIndex index = timeIndices.computeIfAbsent(filePath, k -> new MessageTimeIndex());

    try {
        LocalDateTime messageTime = LocalDateTime.parse(
                message.getTimestamp(), STORAGE_TIME_FORMATTER);

        index.getTimeToFileMap().put(messageTime, filePath);
        index.getTimeToPositionMap().put(messageTime, position);
        index.updateFileTimeRange(filePath, messageTime);

        // 使用 merge 来更新消息计数
        index.getFileMessageCount().merge(filePath, 1, Integer::sum);

        log.debug("Updated time index for message: {} at position: {} in file: {}",
                message.getMessageId(), position, filePath);
    } catch (Exception e) {
        log.error("Error updating time index for message: {} in file: {}",
                message.getMessageId(), filePath, e);
    }
}

/**
 * 查询私聊消息
 */
public ChatMessageVo findPrivateMessagesByTimeRange(
        String userId, String targetUserId, LocalDateTime startTime, LocalDateTime endTime, int page) {
    List<ChatMessage> messages = new ArrayList<>();
    int total = 0;

    try {
        // 获取私聊目录
        String chatDirName = getPrivateMessageFileName(userId, targetUserId);
        String altChatDirName = getPrivateMessageFileName(targetUserId, userId);
        Path chatDir = Paths.get(baseStoragePath, PRIVATE_CHAT_DIR, chatDirName);
        Path altChatDir = Paths.get(baseStoragePath, PRIVATE_CHAT_DIR, altChatDirName);

        log.info("Looking for private messages in directories: {} and {}", chatDir, altChatDir);

        // 使用 TreeMap 确保消息按时间排序
        TreeMap<LocalDateTime, ChatMessage> timeOrderedMessages = new TreeMap<>();

        // 处理两个可能的目录
        for (Path dir : Arrays.asList(chatDir, altChatDir)) {
            if (Files.exists(dir)) {
                // 获取目录下的所有消息文件
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "messages_*.txt")) {
                    for (Path messageFile : stream) {
                        String indexKey = messageFile.toString(); // 使用完整路径作为索引键
                        log.info("Processing message file: {}", messageFile);

                        MessageTimeIndex index = timeIndices.get(indexKey);
                        if (index == null) {
                            log.info("Building time index for file: {}", messageFile);
                            buildTimeIndex(messageFile.toString());
                            index = timeIndices.get(indexKey);
                            if (index == null) {
                                log.error("Failed to build time index for file: {}", indexKey);
                                continue;
                            }
                        }

                        // 获取时间范围内的消息位置
                        NavigableMap<LocalDateTime, Long> positions = index.getTimeToPositionMap()
                                .subMap(startTime, true, endTime, true);

                        // 读取消息
                        try (RandomAccessFile raf = new RandomAccessFile(messageFile.toFile(), "r")) {
                            for (Map.Entry<LocalDateTime, Long> entry : positions.entrySet()) {
                                try {
                                    raf.seek(entry.getValue());
                                    String jsonLine = readMessage(raf);
                                    if (jsonLine != null) {
                                        ChatMessage message = objectMapper.readValue(jsonLine, ChatMessage.class);
                                        // 确保消息属于这两个用户之间的对话
                                        if ((message.getFromUserId().equals(userId) && message.getToUserId().equals(targetUserId)) ||
                                                (message.getFromUserId().equals(targetUserId) && message.getToUserId().equals(userId))) {
                                            timeOrderedMessages.put(entry.getKey(), message);
                                            log.debug("Added message: {}", message.getMessageId());
                                        }
                                    }
                                } catch (Exception e) {
                                    log.error("Error reading message at position {} in file {}: {}",
                                            entry.getValue(), indexKey, e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

        // 计算总数
        total = timeOrderedMessages.size();

        // 分页获取消息
        if (total > 0) {
            List<ChatMessage> allMessages = new ArrayList<>(timeOrderedMessages.values());
            //Collections.reverse(allMessages); // 按时间倒序排列

            int fromIndex = page * PAGE_SIZE;
            int toIndex = Math.min(fromIndex + PAGE_SIZE, total);

            if (fromIndex < total) {
                messages = allMessages.subList(fromIndex, toIndex);
            }
        }

        log.info("Retrieved {} messages out of total {} for private chat between {} and {}",
                messages.size(), total, userId, targetUserId);

    } catch (IOException e) {
        log.error("Error searching private messages by time range", e);
    }

    return new ChatMessageVo(messages, total, page, PAGE_SIZE);
}
/**
 * 按时间范围分页查询活动群聊消息
 * @param activityId 活动ID
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @param page 页码
 */
public ChatMessageVo findActivityMessagesByTimeRange(
        String activityId, LocalDateTime startTime, LocalDateTime endTime, int page) {
    List<ChatMessage> messages = new ArrayList<>();
    int total = 0;

    try {
        // 获取活动群聊目录
        Path chatDir = Paths.get(baseStoragePath, ACTIVITY_CHAT_DIR, activityId);
        log.info("正在活动群聊目录中查找消息，时间范围: {} 到 {}",
            startTime.format(DATE_TIME_FORMATTER),
            endTime.format(DATE_TIME_FORMATTER));

        // 使用 TreeMap 确保消息按时间排序
        TreeMap<LocalDateTime, ChatMessage> timeOrderedMessages = new TreeMap<>();

        if (Files.exists(chatDir)) {
            // 获取目录下的所有消息文件
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(chatDir, "messages_*.txt")) {
                for (Path messageFile : stream) {
                    String indexKey = messageFile.toString();
                    log.info("正在处理消息文件：{}", messageFile);

                    // 获取或构建时间索引
                    MessageTimeIndex index = timeIndices.get(indexKey);
                    if (index == null) {
                        buildTimeIndex(messageFile.toString());
                        index = timeIndices.get(indexKey);
                        if (index == null) {
                            log.error("无法为文件构建时间索引：{}", indexKey);
                            continue;
                        }
                    }

                    // 获取时间范围内的消息位置
                    NavigableMap<LocalDateTime, Long> positions = index.getTimeToPositionMap()
                            .subMap(startTime, true, endTime, true);
                    log.info("在文件{}中找到{}条符合时间范围的消息", messageFile.getFileName(), positions.size());

                    // 读取消息
                    if (!positions.isEmpty()) {
                        try (RandomAccessFile raf = new RandomAccessFile(messageFile.toFile(), "r")) {
                            for (Map.Entry<LocalDateTime, Long> entry : positions.entrySet()) {
                                raf.seek(entry.getValue());
                                String jsonLine = readMessage(raf);
                                if (jsonLine != null) {
                                    try {
                                        ChatMessage message = objectMapper.readValue(jsonLine, ChatMessage.class);
                                        LocalDateTime messageTime = LocalDateTime.parse(
                                            message.getTimestamp(),
                                            DATE_TIME_FORMATTER
                                        );

                                        // 验证消息是否在时间范围内
                                        if (messageTime.isAfter(startTime.minusSeconds(1)) &&
                                            messageTime.isBefore(endTime.plusSeconds(1))) {
                                            // 只添加属于该活动的消息
                                            if (message.getActivityId() != null &&
                                                message.getActivityId().equals(activityId)) {
                                                timeOrderedMessages.put(messageTime, message);
                                                log.debug("Added message: {}", message.getMessageId());
                                            }
                                        }
                                    } catch (Exception e) {
                                        log.error("Error parsing message: {}", jsonLine, e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            log.warn("活动聊天目录不存在: {}", chatDir);
        }

        // 计算总数并分页
        total = timeOrderedMessages.size();
        log.info("找到总消息数: {}", total);

        if (total > 0) {
            List<ChatMessage> allMessages = new ArrayList<>(timeOrderedMessages.values());
//            Collections.reverse(allMessages); // 按时间倒序排列

            int fromIndex = page * PAGE_SIZE;
            int toIndex = Math.min(fromIndex + PAGE_SIZE, total);

            if (fromIndex < total) {
                messages = allMessages.subList(fromIndex, toIndex);
                log.info("返回第{}页消息，共{}条", page, messages.size());
            }
        }

    } catch (IOException e) {
        log.error("查询活动群聊消息时出错", e);
    }

    return new ChatMessageVo(messages, total, page, PAGE_SIZE);
}
//public String uploadImage(MultipartFile file, Long userId) {
//    try {
//
//        if("image/png".equals(file.getContentType())||"image/jpeg".equals(file.getContentType())||"image/jpg".equals(file.getContentType())){
//            // 1.生成唯一文件名
//            String dateDir = DateUtil.format(new Date(), "yyyyMMdd");
//            String uFileName = dateDir+"/"+UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();
//
//            // 2.获取文件流和文件大小
//            InputStream inputStream = file.getInputStream();
//            long fileSize = file.getSize();
//
//            // 3.上传文件到 MinIO
//            minioClient.putObject(
//                    PutObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object(uFileName)
//                            .stream(inputStream, fileSize, -1)
//                            .contentType(file.getContentType()) // 设置文件内容类型
//                            .build()
//            );
//
//            String url=getObjectURL(bucketName,uFileName);
//            // 返回文件的 URL
//            return url;
//        }else {
//            return "400";
//        }
//
//    } catch (MinioException e) {
//        throw new RuntimeException("上传文件到 MinIO 失败: " + e.getMessage());
//    } catch (Exception e) {
//        throw new RuntimeException("上传文件失败", e);
//    }
//}
//public String getObjectURL(String bucketName, String objectName) throws Exception{
//    String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
//            .bucket(bucketName)
//            .object(objectName)
//            .method(Method.GET)
//            .build());
//    return url;
//}
}