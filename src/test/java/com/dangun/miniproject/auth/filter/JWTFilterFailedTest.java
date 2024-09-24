package com.dangun.miniproject.auth.filter;

import com.dangun.miniproject.auth.exception.ReissueAccessTokenException;
import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.auth.service.impl.TokenBlackListService;
import com.dangun.miniproject.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JWTFilterFailedTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private TokenBlackListService tokenBlackListService;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private JWTFilter jwtFilter;

    @Test
    @DisplayName("AccessToken 검증 실패 - 변조된 AccessToken")
    void testJwtFilter_NotAccessToken() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String dummyToken = "dummyToken";
        request.addHeader("Authorization", "Bearer " + dummyToken);

        when(jwtUtil.isExpiredTokenAccess(dummyToken)).thenThrow(new SignatureException("Invalid signature"));

        FilterChain emptyFilterChain = (servletRequest, servletResponse) -> {
        };

        JWTExceptionHandlerFilter exceptionHandlerFilter = new JWTExceptionHandlerFilter();

        FilterChain customFilterChain = (req, res) -> exceptionHandlerFilter.doFilter(req, res, (req2, res2)
                -> jwtFilter.doFilter(req2, res2, emptyFilterChain));

        // When
        customFilterChain.doFilter(request, response);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("유효하지 않은 토큰 서명입니다.", response.getContentAsString());
    }

    @Test
    @DisplayName("AccessToken 검증 실패 - 유효하지 않은 AccessToken")
    void testJwtFilter_invalidAccessToken() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String dummyToken = "dummyToken";
        request.addHeader("Authorization", "Bearer " + dummyToken);

        when(jwtUtil.isExpiredTokenAccess(dummyToken)).thenThrow(new JwtException("유효하지 않은 JWT 토큰입니다."));

        FilterChain emptyFilterChain = (servletRequest, servletResponse) -> {
        };

        JWTExceptionHandlerFilter exceptionHandlerFilter = new JWTExceptionHandlerFilter();

        FilterChain customFilterChain = (req, res) -> exceptionHandlerFilter.doFilter(req, res, (req2, res2)
                -> jwtFilter.doFilter(req2, res2, emptyFilterChain));

        // When
        customFilterChain.doFilter(request, response);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("유효하지 않은 JWT 토큰입니다.", response.getContentAsString());
    }

    @Test
    @DisplayName("AccessToken 검증 실패 - 예상하지 못한 예외")
    void testJwtFilter_Exception() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String dummyToken = "dummyToken";
        request.addHeader("Authorization", "Bearer " + dummyToken);

        when(jwtUtil.isExpiredTokenAccess(dummyToken)).thenThrow(new RuntimeException("JWT 요청 처리 중 에러가 발생했습니다.")); // Mocking

        FilterChain emptyFilterChain = (servletRequest, servletResponse) -> {
        };

        JWTExceptionHandlerFilter exceptionHandlerFilter = new JWTExceptionHandlerFilter();

        FilterChain customFilterChain = (req, res) -> {
            exceptionHandlerFilter.doFilter(req, res, (req2, res2) -> {
                jwtFilter.doFilter(req2, res2, emptyFilterChain);
            });
        };

        // When
        exceptionHandlerFilter.doFilter(request, response, customFilterChain); // 예외 핸들러 필터가 호출되도록 수정

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("JWT 요청 처리 중 에러가 발생했습니다.", response.getContentAsString());
    }

    @Test
    @DisplayName("AccessToken 검증 실패 - 예상하지 못한 예외")
    void testJwtFilter_UnExpected_Exception() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String dummyToken = "dummyToken";
        request.addHeader("Authorization", "Bearer " + dummyToken);

        when(jwtUtil.isExpiredTokenAccess(dummyToken)).thenThrow(mock(ExpiredJwtException.class)); // Mocking

        FilterChain emptyFilterChain = (servletRequest, servletResponse) -> {};

        JWTExceptionHandlerFilter exceptionHandlerFilter = new JWTExceptionHandlerFilter();

        FilterChain customFilterChain = (req, res) -> {
            exceptionHandlerFilter.doFilter(req, res, (req2, res2) -> {
                jwtFilter.doFilter(req2, res2, emptyFilterChain);
            });
        };

        // When && then
        Assertions.assertThatThrownBy(() -> exceptionHandlerFilter.doFilter(request, response, customFilterChain))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("AccessToken 검증 실패 - AccessToken 블랙 리스트")
    void testJwtFilter_tokenIsBlackList() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String blackListAccessToken = "blackListAccessToken";
        request.addHeader("Authorization", "Bearer " + blackListAccessToken);

        when(tokenBlackListService.isBlackListToken(blackListAccessToken)).thenReturn(true);

        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        jwtFilter.doFilter(request, response, mockFilterChain);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("accessToken is blackList contains.", response.getContentAsString());
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("AccessToken 검증 실패 - Authorization Null")
    void testJwtFilter_AuthorizationNull() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String accessToken = "accessToken";
        request.addHeader("null", "Bearer " + accessToken);

        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        jwtFilter.doFilter(request, response, mockFilterChain);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("authorization null", response.getContentAsString());
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("AccessToken 검증 실패 - Bearer prefix 없음")
    void testJwtFilter_noBearerPrefix() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String accessToken = "accessToken";
        request.addHeader("Authorization", accessToken);

        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        jwtFilter.doFilter(request, response, mockFilterChain);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("authorization null", response.getContentAsString());
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("AccessToken 검증 실패 - AccessToken Null")
    void testJwtFilter_AccessTokenNull() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer ");

        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        jwtFilter.doFilter(request, response, mockFilterChain);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("accessToken null", response.getContentAsString());
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("AccessToken 검증 실패 - AccessToken Empty")
    void testJwtFilter_AccessTokenEmpty() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer   ");

        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        jwtFilter.doFilter(request, response, mockFilterChain);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("accessToken null", response.getContentAsString());
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("AccessToken 검증 실패 - AccessToken category is not accessToken")
    void testJwtFilter_NotAccessTokenCategory() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String invalidAccessToken = "invalidAccessToken";
        request.addHeader("Authorization", "Bearer " + invalidAccessToken);

        when(jwtUtil.getJwtCategory(invalidAccessToken)).thenReturn("NotAccess");

        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        jwtFilter.doFilter(request, response, mockFilterChain);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("not accessToken", response.getContentAsString());
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("AccessToken 검증 실패 - Member Null")
    void testJwtFilter_MemberNull() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String accessToken = "accessToken";
        request.addHeader("Authorization", "Bearer " + accessToken);

        String email = "test@naver.com";
        when(jwtUtil.getJwtCategory(accessToken)).thenReturn("accessToken");
        when(jwtUtil.getMemberEmail(accessToken)).thenReturn(email);
        when(memberRepository.findByEmail(email)).thenReturn(null);
        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        jwtFilter.doFilter(request, response, mockFilterChain);

        // Then
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals("not found member", response.getContentAsString());
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }


    // AccessToken 재발급 --------------------------

    @Test
    @DisplayName("AccessToken 재발급 실패 - RefreshToken Expired")
    void testJwtFilter_refreshTokenExpire() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String expireAccessToken = "expireAccessToken";
        String expireRefreshToken = "expireRefreshToken";

        request.addHeader("Authorization", "Bearer " + expireAccessToken);

        Cookie cookie = new Cookie("refreshToken", expireRefreshToken);
        request.setCookies(cookie);

        when(jwtUtil.isExpiredTokenAccess(expireAccessToken)).thenReturn(true);
        when(jwtUtil.isExpiredTokenRefresh(expireRefreshToken)).thenReturn(true);

        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        Assertions.assertThatThrownBy(() -> jwtFilter.doFilter(request, response, mockFilterChain)).isInstanceOf(ReissueAccessTokenException.class).hasMessage("refreshToken expired");

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("refreshToken expired", response.getContentAsString());
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("AccessToken 재발급 실패 - RefreshToken Null")
    void testJwtFilter_RefreshTokenNull() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String expireAccessToken = "expireAccessToken";
        request.addHeader("Authorization", "Bearer " + expireAccessToken);

        Cookie cookie = new Cookie("refreshToken", null);
        request.setCookies(cookie);

        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        ReissueAccessTokenException exception = assertThrows(ReissueAccessTokenException.class, () -> {
            jwtFilter.reissueAccessToken(request, response, mockFilterChain);
        });

        // Then
        assertEquals("refreshToken null", exception.getMessage());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("AccessToken 재발급 실패 - Member Null")
    void testJwtFilter_MemberNullRefreshToken() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String accessToken = "accessToken";
        request.addHeader("Authorization", "Bearer " + accessToken);

        String refreshToken = "refreshToken";
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        request.setCookies(cookie);

        String email = "test@test.com";
        when(jwtUtil.getMemberEmail(refreshToken)).thenReturn(email);
        when(memberRepository.findByEmail(email)).thenReturn(null);

        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            jwtFilter.reissueAccessToken(request, response, mockFilterChain);
        });

        // Then
        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("AccessToken 재발급 실패 - Cookie null")
    void testJwtFilter_CookieNull() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        Cookie cookie = new Cookie("notRefreshToken", null);
        request.setCookies(cookie);
        FilterChain mockFilterChain = mock(FilterChain.class);

        // when & then
        Assertions.assertThatThrownBy(
                        () -> jwtFilter.reissueAccessToken(request, response, mockFilterChain))
                .isInstanceOf(ReissueAccessTokenException.class)
                .hasMessage("refreshToken null");
    }

    @Test
    @DisplayName("AccessToken 재발급 실패 - Cookie notNull, refreshToken null")
    void testJwtFilter_CookieNotNull_RefreshNull() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain mockFilterChain = mock(FilterChain.class);

        // when & then
        Assertions.assertThatThrownBy(
                        () -> jwtFilter.reissueAccessToken(request, response, mockFilterChain))
                .isInstanceOf(ReissueAccessTokenException.class)
                .hasMessage("cookie null");

    }
}