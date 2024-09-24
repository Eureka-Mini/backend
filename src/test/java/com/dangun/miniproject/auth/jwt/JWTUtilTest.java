package com.dangun.miniproject.auth.jwt;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JWTUtilTest {

    private JWTUtil jwtUtil;
    private final String secretKey = "dpfwnldbfpzkrkqhwkrndpfwnldbfpzkrkqhwkrndpfwnldbfpzkrkqhwkrn";

    @BeforeEach
    void setUp() {
        jwtUtil = new JWTUtil(secretKey);
    }

    @Test
    @DisplayName("JWT 생성 및 이메일 추출")
    void testCreateJwtAndExtractEmail() {
        // Given
        String email = "test@test.com";
        String nickname = "tester";
        String accessToken = jwtUtil.createJwtAccess("accessToken", email, nickname, 10000L);

        // When
        String extractedEmail = jwtUtil.getMemberEmail(accessToken);

        // Then
        assertEquals(email, extractedEmail);
    }

    @Test
    @DisplayName("JWT 생성 및 닉네임 추출")
    void testCreateJwtAndExtractNickname() {
        // Given
        String email = "test@test.com";
        String nickname = "tester";
        String accessToken = jwtUtil.createJwtAccess("accessToken", email, nickname, 10000L);

        // When
        String extractedNickname = jwtUtil.getMemberNickname(accessToken);

        // Then
        assertEquals(nickname, extractedNickname);
    }

    @Test
    @DisplayName("JWT 카테고리 추출")
    void testExtractCategory() {
        // Given
        String accessToken = jwtUtil.createJwtAccess("accessToken", "test@test.com", "tester", 10000L);

        // When
        String category = jwtUtil.getJwtCategory(accessToken);

        // Then
        assertEquals("accessToken", category);
    }

    @Test
    @DisplayName("JWT 만료 검사")
    void testExpiredToken() {
        // Given
        String expiredAccessToken = jwtUtil.createJwtAccess("accessToken", "test@test.com", "tester", -1000L);

        // When
        boolean isExpired = jwtUtil.isExpiredTokenAccess(expiredAccessToken);

        // Then
        assertTrue(isExpired);
    }

    @Test
    @DisplayName("JWT 유효한 토큰 검사")
    void testValidToken() {
        // Given
        String validAccessToken = jwtUtil.createJwtAccess("accessToken", "test@test.com", "tester", 10000L);

        // When
        boolean isExpired = jwtUtil.isExpiredTokenAccess(validAccessToken);

        // Then
        assertFalse(isExpired);
    }

    @Test
    @DisplayName("잘못된 JWT 예외 처리")
    void testInvalidToken() {
        // Given
        String invalidToken = "invalidToken";

        // When & Then
        assertThrows(JwtException.class, () -> jwtUtil.isExpiredTokenAccess(invalidToken));
    }

    @Test
    @DisplayName("만료된 Refresh Token 만료 검사")
    void testExpiredRefreshToken() {
        // Given
        String expiredRefreshToken = jwtUtil.createJwtRefresh("refreshToken", "email", -100L);

        // When
        boolean isExpired = jwtUtil.isExpiredTokenRefresh(expiredRefreshToken);

        // Then
        assertTrue(isExpired);
    }

    @Test
    @DisplayName("만료되지 않은 Refresh Token 검사")
    void testNotExpiredRefreshToken() {
        // Given
        String expiredRefreshToken = jwtUtil.createJwtRefresh("refreshToken", "email", 100000L);

        // When
        boolean isExpired = jwtUtil.isExpiredTokenRefresh(expiredRefreshToken);

        // Then
        assertFalse(isExpired);
    }
}