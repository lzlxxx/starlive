package com.starlive.org.util;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 时间戳工具类
 */
public class TimeStampUtils {
//    获取系统默认时区
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /**
     * 返回当前时间戳
     * @param precision 精度，1 表示秒级精度，其他表示毫秒级精度
     */
    public static long getCurrentTimestamp(long precision) {
        Instant now = Instant.now();
        if (precision == ChronoUnit.MILLIS.getDuration().getSeconds()) {
            return now.toEpochMilli();
        } else {
            return now.getEpochSecond();
        }
    }

    /**
     * 格式化时间戳为指定格式
     * @param timestamp 时间戳
     * @param formatter 格式
     */

    public static String formatTimestamp(long timestamp, DateTimeFormatter formatter) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE_ID).format(formatter);
    }
    /**
     * 格式化时间戳为默认格式
     * @param timestamp 时间戳
     */
    public static String formatTimestamp(long timestamp) {
        return formatTimestamp(timestamp, DEFAULT_FORMATTER);
    }
    /**
     * 解析字符串为时间戳。
     * @param timestampStr 时间字符串
     */
    public static long parseTimestamp(String timestampStr) {
        LocalDateTime dateTime = LocalDateTime.parse(timestampStr, DEFAULT_FORMATTER);
        return dateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 在时间戳基础上增加指定天数。
     * @param timestamp 时间戳
     * @param days 要增加的天数
     */
    public static long addDays(long timestamp, long days) {
        return Instant.ofEpochMilli(timestamp).plus(days, ChronoUnit.DAYS).toEpochMilli();
    }

    public static long addMinutes(long timestamp, long minutes) {
        return Instant.ofEpochMilli(timestamp).plus(minutes, ChronoUnit.MINUTES).toEpochMilli();
    }

    public static long addSeconds(long timestamp, long seconds) {
        return Instant.ofEpochMilli(timestamp).plus(seconds, ChronoUnit.SECONDS).toEpochMilli();
    }

    /**
     * 验证时间戳是否在合理范围内。
     * @param timestamp 时间戳
     * @param threshold 阀值
     *
     */

    public static boolean validateTimestamp(long timestamp, long threshold) {
        long currentTimestamp = Instant.now().toEpochMilli();
        return timestamp >= 0 && timestamp <= currentTimestamp + threshold;
    }

    /**
     * 转换时区
     * @param timestamp 时间戳
     * @param sourceZone 原时区
     * @param targetZone 目标时区
     */

    public static long convertTimeZone(long timestamp, ZoneId sourceZone, ZoneId targetZone) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime sourceDateTime = LocalDateTime.ofInstant(instant, sourceZone);
        return sourceDateTime.atZone(targetZone).toInstant().toEpochMilli();
    }

}
