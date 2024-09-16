package com.dangun.miniproject.auth;


import com.dangun.miniproject.auth.exception.exceptions.DuplicateEmailException;
import com.dangun.miniproject.auth.exception.exceptions.DuplicateNicknameException;
import com.dangun.miniproject.auth.exception.exceptions.InvalidEmailException;
import com.dangun.miniproject.auth.service.impl.AuthServiceImpl;
import com.dangun.miniproject.fixture.AddressFixture;
import com.dangun.miniproject.fixture.MemberFixture;
import com.dangun.miniproject.member.domain.Address;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetAddressRequest;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import com.dangun.miniproject.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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

        when(memberRepository.existsByEmail(member.getEmail())).thenReturn(false);
        when(memberRepository.existsByNickname(member.getNickname())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(member.getPassword())).thenReturn("encodedPassword");

        // When
        Member result = authService.signupMember(memberRequest);

        // Then
        assertThat(result).isNotNull();
        verify(memberRepository, times(1)).save(result);
    }

    @Test
    @DisplayName("잘못된 이메일 양식 예외 발생")
    void testSignupMember_InvalidEmail() {
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email("invalid")
                .nickname("nickname")
                .password("password")
                .address(GetAddressRequest.builder()
                        .street("street")
                        .detail("detail")
                        .zipcode("zipcode")
                        .build())
                .build();

        // When
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> {
            authService.validateMember(memberRequest);
        });

        // Then
        assertEquals("유효하지 않은 이메일 형식입니다.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("닉네임 공란 예외 발생")
    void testSignupMember_IllegalArgumentNickname() {
        // Given
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email("test@gmail.com")
                .nickname("")
                .password("password")
                .address(GetAddressRequest.builder()
                        .street("street")
                        .detail("detail")
                        .zipcode("zipcode")
                        .build())
                .build();

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.validateMember(memberRequest);
        });

        // Then
        assertEquals("닉네임 값을 입력해주세요.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("비밀번호 공란 예외 발생")
    void testSignupMember_IllegalArgumentPassword() {
        // Given
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email("test@gmail.com")
                .nickname("nickname")
                .password("")
                .address(GetAddressRequest.builder()
                        .street("street")
                        .detail("detail")
                        .zipcode("zipcode")
                        .build())
                .build();

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.validateMember(memberRequest);
        });

        // Then
        assertEquals("패스워드 값을 입력해주세요.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일 예외 처리")
    void testSignupMember_DuplicateEmail() {
        // Given
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email("test@gmail.com")
                .password("password")
                .nickname("nickname")
                .address(GetAddressRequest.builder()
                        .street("street")
                        .detail("detail")
                        .zipcode("zipcode")
                        .build())
                .build();

        when(memberRepository.existsByEmail(memberRequest.getEmail())).thenReturn(true);

        // When
        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class, () -> {
            authService.validateMember(memberRequest);
        });

        // Then
        assertEquals("이미 존재하는 이메일 입니다.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("이미 존재하는 닉네임 예외 처리")
    void testSignupMember_DuplicateNickname() {
        // Given
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email("test@gmail.com")
                .password("password")
                .nickname("nickname")
                .address(GetAddressRequest.builder()
                        .street("street")
                        .detail("detail")
                        .zipcode("zipcode")
                        .build())
                .build();

        when(memberRepository.existsByNickname(memberRequest.getNickname())).thenReturn(true);

        // When
        DuplicateNicknameException exception = assertThrows(DuplicateNicknameException.class, () -> {
            authService.validateMember(memberRequest);
        });

        // Then
        assertEquals("이미 존재하는 닉네임 입니다.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }
}
