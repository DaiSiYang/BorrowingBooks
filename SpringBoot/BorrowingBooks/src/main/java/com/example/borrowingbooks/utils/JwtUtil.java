package com.example.borrowingbooks.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtil {

    // 安全提示：生产环境需将密钥存储在安全位置（如环境变量/配置中心），不要硬编码！
    private static final String SECRET_KEY = "2D4A614E645267556B58703273357638792F423F4428472B4B6250655368566D";
    private static final long JWT_EXPIRATION_MS = 86400000; // 24小时
    private static final String AUTHORITIES_KEY = "auth";

    /**
     * 生成JWT Token
     * @param userDetails 用户信息
     * @return Token字符串
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(AUTHORITIES_KEY, userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return buildToken(claims, userDetails.getUsername());
    }
    /**
     * 根据单个字段生成JWT Token
     * @param field 单个字段
     * @return Token字符串
     */
    public String generateTokenFromField(String field) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("field", field);  // 将字段存入claims
        return buildToken(claims, field);  // 使用字段生成Token
    }
    /**
     * 刷新Token
     * @param token 原Token
     * @return 新Token
     */
    public String refreshToken(String token) {
        Claims claims = extractAllClaims(token);
        return buildToken(claims, claims.getSubject());
    }

    /**
     * 基础验证：检查Token的签名和过期时间
     */
    public boolean validateToken(String token) {
        try {
            // 解析Token时会自动验证签名和过期时间
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // 捕获异常（如签名无效、Token过期、格式错误）
            log.debug("Token validation failed: {}", ex.getMessage());
            return false;
        }
    }
    /**
     * 用户关联验证：检查Token中的用户名是否匹配，并扩展用户状态检查
     */
    public boolean validateTokenUser(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
                && userDetails.isEnabled()
                && userDetails.isAccountNonLocked()
                && userDetails.isAccountNonExpired();
    }

    /**
     * 从Token中提取用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    /**
     * 从Token中提取用户ID
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> {
            Object id = claims.get("field");
            return id != null ? id.toString() : null;
        });
    }

    /**
     * 从Token中提取权限列表
     */
    public List<String> extractAuthorities(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(AUTHORITIES_KEY));
    }

    //----------------------------- 底层工具方法 -----------------------------
    private String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

