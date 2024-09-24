package com.dangun.miniproject.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${jwt.secretKey}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);  // HMAC-SHA 알고리즘용 키 생성
    }

    // 토큰 내 member email 추출
    public String getMemberEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().get("email", String.class);
    }

    // 액세스 토큰 내 member nickname 추출
    public String getMemberNickname(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().get("nickname", String.class);
    }

    public String getJwtCategory(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().get("category", String.class);
    }

    public boolean isExpiredTokenAccess(String token) {
        try {
            // 토큰 파싱 및 유효성 검사
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            // 만료 시간 검사
            Date expirationDate = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody().getExpiration();
            return expirationDate.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            throw e;
        }
    }

    // 리프레쉬 토큰 만료 시간 검증
    public boolean isExpiredTokenRefresh(String token) {
        try {
            Jwts.parser().verifyWith(secretKey)
                    .build().parseSignedClaims(token);
        }catch (ExpiredJwtException e){
            return true;
        }
        return false;
    }

    public String createJwtAccess(String category, String email, String nickname, Long expireTime) {
        return Jwts.builder()
                .claim("category", category)
                .claim("email", email)
                .claim("nickname", nickname)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey)
                .compact();
    }

    public String createJwtRefresh(String category, String email, Long expireTime) {
        return Jwts.builder()
                .claim("category", category)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey)
                .compact();
    }
}
