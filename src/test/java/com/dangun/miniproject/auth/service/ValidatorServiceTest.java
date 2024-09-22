package com.dangun.miniproject.auth.service;

import com.dangun.miniproject.auth.exception.exceptions.DuplicateEmailException;
import com.dangun.miniproject.auth.exception.exceptions.DuplicateNicknameException;
import com.dangun.miniproject.auth.exception.exceptions.InvalidEmailException;
import com.dangun.miniproject.auth.service.validator.SignupValidator;
import com.dangun.miniproject.member.dto.GetAddressRequest;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import com.dangun.miniproject.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ValidatorServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private SignupValidator signupValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        signupValidator = new SignupValidator(memberRepository);
    }

    @Test
    @DisplayName("회원 가입 실패 - 잘못된 이메일 양식 예외 발생")
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
            signupValidator.validateMember(memberRequest);
        });

        // Then
        assertEquals("유효하지 않은 이메일 형식입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 가입 실패 - 닉네임 공란 예외 발생")
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
            signupValidator.validateMember(memberRequest);
        });

        // Then
        assertEquals("닉네임 값을 입력해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 가입 실패 - 비밀번호 공란 예외 발생")
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
            signupValidator.validateMember(memberRequest);
        });

        // Then
        assertEquals("비밀번호를 입력해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 가입 실패 - 이미 존재하는 이메일 예외 처리")
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
            signupValidator.validateMember(memberRequest);
        });

        // Then
        assertEquals("이미 존재하는 이메일 입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 가입 실패 - 이미 존재하는 닉네임 예외 처리")
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
            signupValidator.validateMember(memberRequest);
        });

        // Then
        assertEquals("이미 존재하는 닉네임 입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 가입 실패 - 주소 공란 예외 처리")
    void testSignupMember_IllegalArgumentStreet() {
        // Given
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email("test@gmail.com")
                .password("password")
                .nickname("nickname")
                .address(GetAddressRequest.builder()
                        .street("")
                        .detail("detail")
                        .zipcode("zipcode")
                        .build())
                .build();

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            signupValidator.validateMember(memberRequest);
        });

        // Then
        assertEquals("주소를 입력해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 가입 실패 - 상세 주소 공란 예외 처리")
    void testSignupMember_IllegalArgumentDetail() {
        // Given
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email("test@gmail.com")
                .password("password")
                .nickname("nickname")
                .address(GetAddressRequest.builder()
                        .street("street")
                        .detail("")
                        .zipcode("zipcode")
                        .build())
                .build();

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            signupValidator.validateMember(memberRequest);
        });

        // Then
        assertEquals("상세주소를 입력해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 가입 실패 - 우편번호 공란 예외 처리")
    void testSignupMember_IllegalArgumentZipcode() {
        // Given
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email("test@gmail.com")
                .password("password")
                .nickname("nickname")
                .address(GetAddressRequest.builder()
                        .street("street")
                        .detail("detail")
                        .zipcode("")
                        .build())
                .build();

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            signupValidator.validateMember(memberRequest);
        });

        // Then
        assertEquals("우편번호를 입력해주세요.", exception.getMessage());
    }
}
