package com.dangun.miniproject.auth.service;


import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.auth.service.impl.AuthServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;


@ExtendWith(MockitoExtension.class)
public class AuthServiceFailedTest {

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("로그아웃 실패 테스트 - AccessToken Null")
    void testLogoutMember_Failed_TokenNull() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "notBearer ");

        // When
        authService.logoutMember(request, response);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    @DisplayName("로그아웃 실패 테스트 - AccessToken 변조")
    void testLogoutMember_Failed_InvalidToken() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String invalidToken = "invalidToken";
        request.addHeader("Authorization", "Bearer " + invalidToken);

        doThrow(new JwtException("accessToken invalid"))
                .when(jwtUtil).isExpiredTokenAccess(invalidToken);

        // When
        authService.logoutMember(request, response);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }
}
