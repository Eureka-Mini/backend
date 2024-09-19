package com.dangun.miniproject.auth;

import com.dangun.miniproject.auth.filter.JWTExceptionHandlerFilter;
import com.dangun.miniproject.auth.filter.JWTFilter;
import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JWTFilterTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private JWTFilter jwtFilter;

    @Test
    @DisplayName("AccessToken 검증 완료 처리")
    void testJwtFilter_Success() throws Exception{
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = mock(MockFilterChain.class);

        String dummyToken = "dummyToken";
        request.addHeader("Authorization", "Bearer " + dummyToken);

        String token = request.getHeader("Authorization").split(" ")[1];
        String email = "test@gmail.com";

        when(jwtUtil.getJwtCategory(token)).thenReturn("accessToken");
        when(jwtUtil.isExpiredTokenAccess(token)).thenReturn(false);
        when(jwtUtil.getMemberEmail(token)).thenReturn(email);

        Member member = Member.builder()
                        .email(email)
                        .build();
        when(memberRepository.findByEmail(email)).thenReturn(member);

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertEquals(200, response.getStatus());
        verify(jwtUtil).getJwtCategory(token);
        verify(jwtUtil).isExpiredTokenAccess(token);
        verify(jwtUtil).getMemberEmail(token);
        verify(memberRepository).findByEmail(email);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("변조된 AccessToken 예외 처리")
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

        FilterChain customFilterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                exceptionHandlerFilter.doFilter(req, res, new FilterChain() {

                    @Override
                    public void doFilter(ServletRequest req2, ServletResponse res2) throws IOException, ServletException {
                        jwtFilter.doFilter(req2, res2, emptyFilterChain);
                    }
                });
            }
        };

        // When
        customFilterChain.doFilter(request, response);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("유효하지 않은 토큰 서명입니다.", response.getContentAsString());
    }

    @Test
    @DisplayName("만료 된 AccessToken 재발급 완료 처리")
    void testJwtFilter_AccessTokenReissue() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String expiredAccessToken = "expiredAccessToken";
        String validRefreshToken = "validRefreshToken";

        request.addHeader("Authorization", "Bearer " + expiredAccessToken);

        Cookie refreshTokenCookie = new Cookie("refreshToken", validRefreshToken);
        request.setCookies(refreshTokenCookie);

        when(jwtUtil.isExpiredTokenAccess(expiredAccessToken)).thenReturn(true);
        when(jwtUtil.isExpiredTokenRefresh(validRefreshToken)).thenReturn(false);

        String email = "test@gamil.com";
        when(jwtUtil.getMemberEmail(validRefreshToken)).thenReturn(email);

        String newAccessToken = "newAccessToken";
        when(jwtUtil.createJwtAccess("accessToken", email, "nickname", 60 * 60 * 1000L)).thenReturn(newAccessToken);

        Member member = Member.builder()
                .email(email)
                .nickname("nickname")
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(member);

        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        jwtFilter.doFilter(request, response, mockFilterChain);

        // Then
        assertEquals("Bearer " + newAccessToken, response.getHeader("Authorization"));
        verify(mockFilterChain).doFilter(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    @DisplayName("만료 된 AccessToken 재발급 실패 예외 처리")
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

        doThrow(new ExpiredJwtException(null, null, "refresh token expire"))
                .when(jwtUtil).isExpiredTokenRefresh(expireRefreshToken);

        FilterChain mockFilterChain = mock(FilterChain.class);

        // When
        jwtFilter.doFilter(request, response, mockFilterChain);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("refreshToken expired", response.getContentAsString());
        verify(mockFilterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }
}