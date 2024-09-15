package com.dangun.miniproject.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.repository.MemberRepository;
import com.dangun.miniproject.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private GetMemberRequest validMemberRequest;
    private GetMemberRequest invalidEmailMemberRequest;
    private GetMemberRequest existingMemberRequest;
    private GetMemberRequest emptyPasswordMemberRequest;

    @BeforeEach
    void setupMember() {
        validMemberRequest = GetMemberRequest.builder()
                .email("test@example.com")
                .nickname("nickname1")
                .password("password1")
                .build();

        invalidEmailMemberRequest = GetMemberRequest.builder()
                .email("testEmail")
                .nickname("nickname2")
                .password("password2")
                .build();

        existingMemberRequest = GetMemberRequest.builder()
                .email("okay123@example.com")
                .nickname("nickname3")
                .password("password3")
                .build();

        emptyPasswordMemberRequest = GetMemberRequest.builder()
                .email("rnrnrnrnrn@example.com")
                .nickname("nickname4")
                .password("")
                .build();
    }

    @Test
    @DisplayName("회원 가입 완료 테스트")
    void testSignupMember_Success() {
        // Given
        when(memberRepository.existsByEmail(validMemberRequest.getEmail())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(validMemberRequest.getPassword())).thenReturn("encodedPassword");

        // When
        authService.signupMember(validMemberRequest);

        // Then
        verify(memberRepository, times(1)).save(any(Member.class));
    }


    @Test
    @DisplayName("잘못된 이메일 양식 예외 발생")
    void testSignupMember_InvalidEmail() {

        // When
        authService.signupMember(invalidEmailMemberRequest);

        // Then
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일 예외 발생")
    void testSignupMember_ExistedEmail() {
        // Given
        when(memberRepository.existsByEmail(existingMemberRequest.getEmail())).thenReturn(true);

        // When
        authService.signupMember(existingMemberRequest);

        // Then
        verify(memberRepository, never()).save(any(Member.class));
    }


    @Test
    @DisplayName("비밀번호 미입력 시 예외 발생")
    void testSignupMember_EmptyPassword() {
        // Given

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.signupMember(emptyPasswordMemberRequest);
        });

        assertEquals("비밀번호를 입력해주세요.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }
}
