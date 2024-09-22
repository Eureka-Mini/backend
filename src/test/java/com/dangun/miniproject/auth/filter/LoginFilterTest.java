package com.dangun.miniproject.auth.filter;

import com.dangun.miniproject.auth.dto.UserDetailsDto;
import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.member.domain.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class LoginFilterTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private LoginFilter loginFilter;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup()
                .addFilter((Filter) loginFilter)
                .build();
    }

    @Test
    @DisplayName("로그인 성공 시 JWT 발급 처리")
    void testLoginAuthentication_Success() throws Exception {
        String email = "dummy@naver.com";
        String password = "password1";
        String nickname = "nickname";
        String token = "dummyToken";

        Member member = Member.builder()
                .email(email)
                .nickname("nickname")
                .password("encodedPassword")
                .build();

        UserDetailsDto userDetailsDto = new UserDetailsDto(member);

        // When
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsDto, password, userDetailsDto.getAuthorities());
        when(authenticationManager
                .authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // authentication 통과 후 jwt 발급
        when(jwtUtil.createJwtAccess("accessToken", email, nickname, 60 * 60 * 1000L)).thenReturn(token);
        when(jwtUtil.createJwtRefresh("refreshToken", email, 7 * 24 * 60 * 60 * 1000L)).thenReturn(token);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(Collections.singletonMap("email", email))))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer " + token))
                .andExpect(cookie().exists("refreshToken"));
    }

    @Test
    @DisplayName("로그인 실패 시 401 에러 코드 응답")
    void testLoginAuthentication_Failed() throws Exception {
        // Given
        String email = "wrong@example.com";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UsernameNotFoundException("Authentication failed"));

        // When & Then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(Collections.singletonMap("email", email))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입출력 오류 시 BadCredentialsException 테스트")
    void testLoginAuthentication_FailedDueToIOException() throws Exception {
        // given
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getInputStream()).thenThrow(new IOException("Input stream error"));

        // when & then
        assertThrows(BadCredentialsException.class, () -> {
            // 메서드를 실행하여 예외 발생 여부 확인
            loginFilter.attemptAuthentication(mockRequest, null);
        });
    }
}
