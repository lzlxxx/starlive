package com.starlive.org.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {


    @Test
    //测试  检查字符串是否为null或空字符串
    public void testStringIsNull(){
        System.out.println(StringUtil.stringIsNull(""));
        String s = null;
        System.out.println(StringUtil.stringIsNull(s));
        System.out.println(StringUtil.stringIsNull("aa"));
    }

    @Test
    //检查字符串是否为null、空字符串或仅包含空白字符（如空格、制表符、换行符等）
    public void testStringIsNullOrBlank(){
        System.out.println(StringUtil.stringIsNullOrBlank("        \n "));
        System.out.println(StringUtil.stringIsNullOrBlank("    a    \n "));
    }

    @Test
    //检查字符串是否非null且非空白
    public void testStringNotNull(){
        System.out.println(StringUtil.stringNotNull(" "));
        System.out.println(StringUtil.stringNotNull(""));
        System.out.println(StringUtil.stringNotNull(null));
        System.out.println(StringUtil.stringNotNull(" a"));
    }

    @Test
    //去除字符串两端的空白字符
    public void TestStringTrim(){
        System.out.println(StringUtil.stringTrim("  a   "));
        System.out.println(StringUtil.stringTrim(" a a "));
    }

    @Test
    //去除字符串两端的空白字符，如果字符串为null或空，则返回空字符串""。
    public void TestStringTrimReturnNull(){
        System.out.println(StringUtil.stringTrimReturnNull(null));
        System.out.println(StringUtil.stringTrim("  a   "));
        System.out.println(StringUtil.stringTrim(" a a "));
    }

    @Test
    //去除字符串两端的指定字符集合中的字符
    public void TestTrimStringCharacters(){
        System.out.println(StringUtil.trimStringCharacters("abcde", "ae"));
        System.out.println(StringUtil.trimStringCharacters("---Hello, World!---", "H-"));
        System.out.println(StringUtil.trimStringCharacters("##Hello, World!##", "#"));
        System.out.println(StringUtil.trimStringCharacters("123abc123", "123"));
        System.out.println(StringUtil.trimStringCharacters(null, "123"));
    }

    @Test
    //去除字符串前端的指定字符集合中的字符
    public void TestTrimLeadingCharacters(){
        System.out.println(StringUtil.trimLeadingCharacters("---Hello, World!---", "-#") );
        System.out.println(StringUtil.trimLeadingCharacters("##Hello, World!##", "#!"));
        System.out.println(StringUtil.trimLeadingCharacters("123abc123", "123"));
        System.out.println(StringUtil.trimLeadingCharacters(null, "123"));
    }

    @Test
    //去除字符串后端的指定字符集合中的字符
    public void TestTrimAfterCharacters(){
        System.out.println(StringUtil.trimAfterCharacters("---Hello, World!---", "-#"));
        System.out.println(StringUtil.trimAfterCharacters("##Hello, World!##", "#!"));
        System.out.println(StringUtil.trimAfterCharacters("123abc123", "123"));
        System.out.println(StringUtil.trimAfterCharacters(null, "123"));
    }

    @Test
    //查询字符串中是否包含某个子串
    public void containsSubString(){
        System.out.println(StringUtil.containsSubString("Hello, World!", "World"));
        System.out.println(StringUtil.containsSubString("Hello, World!", "world"));
        System.out.println(StringUtil.containsSubString("Java programming", "prog"));
        System.out.println(StringUtil.containsSubString(null, "prog"));
        System.out.println(StringUtil.containsSubString("Java programming", null));
    }

    @Test
    //返回子串首次出现的位置,不存在返回-1
    public void TestIndexOfSubstring() {
        System.out.println(StringUtil.indexOfSubstring("Hello, World!", "World"));
        System.out.println(StringUtil.indexOfSubstring("Hello, World!", "world"));
        System.out.println(StringUtil.indexOfSubstring("Java programming", "prog"));
        System.out.println(StringUtil.indexOfSubstring(null, "prog"));
        System.out.println(StringUtil.indexOfSubstring("Java programming", null));
    }

    @Test
    //比较两个字符串是否相等，忽略大小写
    public void TestEqualsIgnoreCase(){
        System.out.println(StringUtil.equalsIgnoreCase("Hello", "hello"));
        System.out.println(StringUtil.equalsIgnoreCase("World", "WORLD"));
        System.out.println(StringUtil.equalsIgnoreCase("Java", "Java "));
        System.out.println(StringUtil.equalsIgnoreCase(null, null));
        System.out.println(StringUtil.equalsIgnoreCase("", ""));
    }

    @Test
    //翻转字符串中的字符大小写
    public void TestReverseCase() {
        System.out.println(StringUtil.reverseCase("Hello, World!"));   // 输出: hELLO, wORLD!
        System.out.println(StringUtil.reverseCase("Java Programming")); // 输出: jAVA pROGRAMMING
        System.out.println(StringUtil.reverseCase("123ABCdef"));        // 输出: 123abcDEF
        System.out.println(StringUtil.reverseCase(null));
        System.out.println(StringUtil.reverseCase(""));
    }

    @Test
    //验证字符串是否符合电话号码的格式
    public void isValidPhoneNumber() {
        System.out.println(StringUtil.isValidPhoneNumber("15240013645"));
        System.out.println(StringUtil.isValidPhoneNumber("l124001364005"));
        System.out.println(StringUtil.isValidPhoneNumber("001364005"));
        System.out.println(StringUtil.isValidPhoneNumber("1524001365"));
        System.out.println(StringUtil.isValidPhoneNumber(""));
        System.out.println(StringUtil.isValidPhoneNumber(null));
    }


    @Test
    //对字符串进行URL编码
    public void TestEncodeUrlUTF_8(){
        System.out.println(StringUtil.encodeUrlUTF_8("Hello World!"));  // 输出: Hello+World%21
        System.out.println(StringUtil.encodeUrlUTF_8("name=John Doe&age=25"));  // 输出: name%3DJohn+Doe%26age%3D25
        System.out.println(StringUtil.encodeUrlUTF_8("https://example.com?q=Java URL encoding"));  // 输出: https%3A%2F%2Fexample.com%3Fq%3DJava+URL+encoding
    }



}