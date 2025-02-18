package com.parking.util.tool;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成token
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        // 根据密钥字符串生成 Key 对象
        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        // 根据密钥字符串生成 Key 对象
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 验证token
     */
    public boolean validateToken(String token) {
        try {
            // 根据密钥字符串生成 Key 对象
            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }
} 