package com.dangun.miniproject.auth.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${jwt.secretKey}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 토큰 내 member email 추출
    public String getMemberEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().get("email", String.class);
    }

    // 만료 시간 검증
    public boolean isExpiredToken(String token) {
        try {
            return  Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody().getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            log.warn("토큰이 만료되었습니다: " + e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("토큰 서명이 유효하지 않습니다: "+ e.getMessage());
            throw e;
        }
    }

    // 토큰 생성
    public String createJwt(String category, String email, Long expireTime) {
        return Jwts.builder()
                .claim("category", category)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis())) // 발행 시간
                .expiration(new Date(System.currentTimeMillis() + expireTime)) // 만료 시간
                .signWith(secretKey) // 시그니쳐
                .compact();
    }

    public String getJwtCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }
}
