package com.starlive.org.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {

//    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static String SECRET_KEY = "WTfjdvhTnceHQJhY3vYBIlhLP6e6gfHqKp0nivc1BtE=";
//private static final SecretKey =
    public static String generateToken(String usernid) {
//        System.out.println("key" +SECRET_KEY);
        return Jwts.builder()
                .setSubject(usernid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))  // 设置过期时间为24小时
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    // 解析令牌的方法
    public static Claims parseToken(String token) {
//        System.out.println("Base64.getEncoder"+Base64.getEncoder().encodeToString(SECRET_KEY.getEncoded()));;
        try{
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        }
    catch (SignatureException e) {
        // 令牌签名验证失败
        throw new RuntimeException("Invalid JWT signature");
    } catch (Exception e) {
        // 其他解析异常
        throw new RuntimeException("Invalid JWT token");
    }

    }
}
