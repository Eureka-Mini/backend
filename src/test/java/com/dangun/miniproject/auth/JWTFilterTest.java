package com.dangun.miniproject.auth;

import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.auth.filter.JWTFilter;
import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JWTFilterTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private JWTFilter jwtFilter;


    @Test
    @DisplayName("JWt Headers Authorization 검증")
    void testJwtFilter_Success() throws Exception{
        String email = "test@test.com";
        String token = "dummyToken";

        when(jwtUtil.isExpiredToken(token)).thenReturn(false); // 만료 검증
        when(jwtUtil.getMemberEmail(token)).thenReturn(email); // payload email 검증
        when(jwtUtil.getJwtCategory(token)).thenReturn("accessToken");

        Member member = Member.builder()
                        .email(email)
                        .build();

        when(memberRepository.findByEmail(email)).thenReturn(member);

        // 직접 mock req, res 객체 생성
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // doFilter 처리
        jwtFilter.doFilterInternal(request, response, filterChain);

        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("JWt Headers Authorization 검증 실패 처리")
    void testJwtFilter_Failed() throws Exception {
        String token = "dummyToken";

        when(jwtUtil.isExpiredToken(token)).thenReturn(true);
        when(jwtUtil.getJwtCategory(token)).thenReturn("notAccessToken");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
    }
}
