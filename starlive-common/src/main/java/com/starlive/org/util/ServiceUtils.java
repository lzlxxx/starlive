package com.starlive.org.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;

/**
 * 基础服务工具类
 */
public class ServiceUtils {
//    高德web api用户key
    private static final String KEY = "0742b4ac8feec65b64f8a3ae7b318c6d";
    /**
     * 获取用户真实ip
     * @param request http请求
     */
    public  String getActualIpAddress(HttpServletRequest request){
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
//  todo ：下两步可以结合前端优化？？
    /**
     * 与高德建立连接
     * @param url 和ip组合后的完成链接
     */

    public JSONObject getInfoFromGeoApi(String url) throws IOException {
        //0.初始化xxx
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        //1.要做的事
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                //1.1
                throw new IOException("连接不成功");
            }
            String responseBody = response.body().string();
            JSONObject result = JSON.parseObject(responseBody);
            return result;
        }

    }

    /**
     *通过ip获取用户真实位置
     * @param request http请求
     */
     public String getUserAddressByIp(HttpServletRequest request) {
         String ipAddress = getActualIpAddress(request);
         String addressCode = "";
         try {
             JSONObject json = getInfoFromGeoApi("http://restapi.amap.com/v3/ip?ip=" + ipAddress + "&key=" + KEY );

             if("0".equals(json.getString("status"))){
//             todo:使用自定义异常替换
                 return "结果返回异常";
             }
             addressCode = json.getString("province");
         } catch (IOException e) {
             throw new RuntimeException(e);
         }

         return addressCode;
     }

    /**
     * 通过ip获取用户经纬度
     * @param request http请求
     */
     public String getUserGeoLocationByIp(HttpServletRequest request){
         String ipAddress = getActualIpAddress(request);
         String rectangle = null;
         try {
             JSONObject json = getInfoFromGeoApi("http://restapi.amap.com/v3/ip?ip=" + ipAddress + "&key=" + KEY );
             if("0".equals(json.getString("status"))){
//             todo:使用自定义异常替换
                 return "结果返回异常";
             }
             rectangle = json.getString("rectangle");
         } catch (IOException e) {
//             todo:使用自定义异常替换
             throw new RuntimeException(e);
         }
         return rectangle;
     }

    /**
     * 逆地理位置编码获取用户具体位置
     * @param rectangle 经纬度
     */
     public String getUserSpecificLocation(String rectangle){
         return null;
     }

}
