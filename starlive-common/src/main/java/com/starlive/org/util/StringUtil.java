package com.starlive.org.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;

public class StringUtil {
    public static  final String EMPTY = "";
    /**
     * 判断字符串是否为空或者为空字符串
     * @param s 目标字符串
     * @return
     */
    public static boolean stringIsNull(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 判断字符串是否为空或者空字符串，或仅包含空白字符(空格、制表符、换行符)
     * @param s
     * @return
     */
    public static boolean stringIsNullOrBlank(String s) {
        return stringIsNull(s) || s.trim().isEmpty();
    }

    /**
     * 检查字符串是否非null且非空白
     * @param s
     * @return
     */
    public static boolean stringNotNull(String s) {
        return !stringIsNullOrBlank(s);
    }

    /**
     * 去除字符串两端的空白字符
     * @param s
     * @return
     */
    public static String stringTrim(String s) {
        if (!stringIsNull(s)) {
            return s.trim();
        }
        return s;
    }

    /**
     * 去除字符串两端的空白字符，如果字符串为null或空，则返回空字符串""。
     * @param s
     * @return
     */
    public static String stringTrimReturnNull(String s) {
        if (stringIsNull(s)) {
            return "";
        }
        return stringTrim(s);
    }

    /**
     * 去除字符串两端的指定字符集合中的字符
     * @param s
     * @param characters
     * @return
     */
    public static String trimStringCharacters(String s, String characters) {
        if (!stringIsNull(s)) {
            HashSet<Character> removeSet = new HashSet<>();

            for (char c : characters.toCharArray()) {
                removeSet.add(c);
            }
            int start = 0;
            int end = s.length() - 1;

            while (start <= end && removeSet.contains(s.charAt(start))) {
                start++;
            }

            while (end >= start && removeSet.contains(s.charAt(end))) {
                end--;
            }

            return s.substring(start, end + 1);
        }
        return s;
    }

    /**
     * 去除字符串前端的指定字符集合中的字符
     * @param s
     * @param characters
     * @return
     */
    public static String trimLeadingCharacters(String s, String characters) {
        if (!stringIsNull(s)) {
            HashSet<Character> removeSet = new HashSet<>();

            for (char c : characters.toCharArray()) {
                removeSet.add(c);
            }
            int start = 0;
            int end = s.length() - 1;

            while (start <= end && removeSet.contains(s.charAt(start))) {
                start++;
            }
            return s.substring(start);
        }
        return s;
    }

    /**
     * 去除字符串后端的指定字符集合中的字符
     * @param s
     * @param characters
     * @return
     */
    public static String trimAfterCharacters(String s, String characters) {
        if (!stringIsNull(s)) {
            HashSet<Character> removeSet = new HashSet<>();

            for (char c : characters.toCharArray()) {
                removeSet.add(c);
            }

            int end = s.length() - 1;

            while (end >= 0 && removeSet.contains(s.charAt(end))) {
                end--;
            }
            return s.substring(0, end + 1);
        }
        return s;
    }

    /**
     * 查询字符串中是否包含某个子串
     * @param s
     * @param sub
     * @return
     */
    public static boolean containsSubString(String s, String sub) {
        if (!stringIsNull(s) && !stringIsNull(sub)) {
            return s.contains(sub);
        }
        return false;
    }

    /**
     * 返回子串首次出现的位置,不存在返回-1
     * @param str
     * @param substring
     * @return
     */
    public static int indexOfSubstring(String str, String substring) {
        if (!stringIsNull(str) && !stringIsNull(substring)) {
            return str.indexOf(substring);
        }
        return -1;
    }

    /**
     * 比较两个字符串是否相等，忽略大小写
     * @param s
     * @param s1
     * @return
     */
    public static boolean equalsIgnoreCase(String s, String s1) {
        if (!stringIsNull(s) && !stringIsNull(s1)) {
            return s.equalsIgnoreCase(s1);
        }
        return false;
    }

    /**
     * 翻转字符串中的字符大小写
     * @param str
     * @return
     */
    public static String reverseCase(String str) {
        if (stringIsNull(str)) {
            return str;
        }

        StringBuilder result = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c)) {
                result.append(Character.toUpperCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 验证字符串是否符合电话号码的格式
     * @param phoneNumber
     * @return
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (stringIsNull(phoneNumber)) {
            return false;
        }
        String regex = "^1[3-9]\\d{9}$";
        return phoneNumber.matches(regex);
    }


    /**
     * 对字符串进行URL编码
     * @param s
     * @return
     */
    public static String encodeUrlUTF_8(String s) {
        if (!stringIsNull(s)) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return s;
    }
}


