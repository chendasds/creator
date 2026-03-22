package com.creation.platform.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {

    /**
     * Token 有效期：2 小时
     * 2 * 60 * 60 * 1000 = 7200000 毫秒
     */
    private static final long EXPIRE_TIME = 2 * 60 * 60 * 1000;

    /**
     * 续期阈值：30 分钟
     * 当 Token 剩余时间不足 30 分钟时，自动续期
     * 30 * 60 * 1000 = 1800000 毫秒
     */
    private static final long RENEW_THRESHOLD = 30 * 60 * 1000;

    /** 密钥（至少 32 字符） */
    private static final String SECRET = "creation-platform-secret-key-for-jwt-authentication-system-2024";

    /**
     * 生成 Token
     *
     * @param userId 用户 ID
     * @return Token 字符串
     */
    public static String generateToken(Long userId) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    /**
     * 解析 Token，获取 Claims
     *
     * @param token Token 字符串
     * @return Claims 对象，解析失败返回 null
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * 校验 Token 是否有效
     *
     * @param token Token 字符串
     * @return true 表示有效，false 表示无效或已过期
     */
    public static boolean validateToken(String token) {
        return parseToken(token) != null;
    }

    /**
     * 获取 Token 剩余有效期（毫秒）
     *
     * @param token Token 字符串
     * @return 剩余有效期，Token 无效返回 0
     */
    public static long getRemainingTime(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return 0;
        }
        Date expiration = claims.getExpiration();
        long remaining = expiration.getTime() - System.currentTimeMillis();
        return Math.max(remaining, 0);
    }

    /**
     * 判断 Token 是否需要续期（剩余时间不足 30 分钟）
     *
     * @param token Token 字符串
     * @return true 表示需要续期，false 表示不需要
     */
    public static boolean needsRenewal(String token) {
        return getRemainingTime(token) < RENEW_THRESHOLD;
    }

    /**
     * 从 Token 中获取用户 ID
     *
     * @param token Token 字符串
     * @return 用户 ID，Token 无效返回 null
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        String subject = claims.getSubject();
        return Long.parseLong(subject);
    }
}
