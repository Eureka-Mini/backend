package com.dangun.miniproject.auth.service;

import com.dangun.miniproject.auth.service.impl.TokenBlackListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TokenBlackListServiceTest {

    @InjectMocks
    private TokenBlackListService tokenBlackListService;

    @Test
    @DisplayName("토큰 블랙 리스트에 등록")
    void testAddBlackListToken() {
        // Given
        String accessToken = "accessToken";

        // When && Then
        tokenBlackListService.addBlackListToken(accessToken);
    }

    @Test
    @DisplayName("해당 토큰 블랙 리스트 O")
    void testIsBlackListTokenTrue() {
        // Given
        String accessToken = "accessToken";

        // When
        tokenBlackListService.addBlackListToken(accessToken);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenBlackListService.addBlackListToken(accessToken);
        });

        // Then
        assertEquals("이미 블랙 리스트에 등록 된 토큰입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("해당 토큰 블랙 리스트 X")
    void testIsBlackListTokenFalse() {
        // Given
        String accessToken = "accessToken";

        // When && Then
        boolean isBlackListToken = tokenBlackListService.isBlackListToken(accessToken);

        assertFalse(isBlackListToken);
    }
}
