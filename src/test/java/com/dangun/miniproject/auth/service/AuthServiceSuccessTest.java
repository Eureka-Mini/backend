package com.dangun.miniproject.auth.service;


import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.auth.service.impl.AuthServiceImpl;
import com.dangun.miniproject.auth.service.impl.TokenBlackListService;
import com.dangun.miniproject.auth.service.validator.SignupValidator;
import com.dangun.miniproject.fixture.AddressFixture;
import com.dangun.miniproject.fixture.MemberFixture;
import com.dangun.miniproject.member.domain.Address;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import com.dangun.miniproject.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceSuccessTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private TokenBlackListService tokenBlackListService;

    @Mock
    private SignupValidator signupValidator;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("회원 가입 완료 테스트")
    void testSignupMember_Success() {
        // Given
        GetMemberRequest memberRequest = MemberFixture.instanceOf();
        Member member = memberRequest.toEntity();

        Address address = AddressFixture.instanceOf(member);
        member.addAddress(address);

        doNothing().when(signupValidator).validateMember(memberRequest);
        when(bCryptPasswordEncoder.encode(member.getPassword())).thenReturn("encodedPassword");

        // When
        Member result = authService.signupMember(memberRequest);

        // Then
        assertThat(result).isNotNull();
        verify(memberRepository, times(1)).save(result);
        verify(signupValidator, times(1)).validateMember(memberRequest);
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void testLogoutMember_Success() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String accessToken = "accessToken";
        request.addHeader("Authorization", "Bearer " + accessToken);

        when(jwtUtil.isExpiredTokenAccess(accessToken)).thenReturn(false);
        doNothing().when(tokenBlackListService).addBlackListToken(accessToken);

        // When
        authService.logoutMember(request, response);

        // Then
        assertEquals(200, response.getStatus());
        verify(tokenBlackListService).addBlackListToken(accessToken);
    }
}
