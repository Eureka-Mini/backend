package com.dangun.miniproject.auth;


import com.dangun.miniproject.auth.exception.exceptions.DuplicateEmailException;
import com.dangun.miniproject.auth.exception.exceptions.InvalidEmailException;
import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.auth.service.impl.AuthServiceImpl;
import com.dangun.miniproject.auth.service.impl.TokenBlackListService;
import com.dangun.miniproject.auth.service.validator.SignupValidator;
import com.dangun.miniproject.fixture.AddressFixture;
import com.dangun.miniproject.fixture.MemberFixture;
import com.dangun.miniproject.member.domain.Address;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetAddressRequest;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import com.dangun.miniproject.member.repository.MemberRepository;
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

    @Test
    @DisplayName("로그아웃 실패 테스트 / token null")
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
    @DisplayName("로그아웃 실패 테스트 / 액세스 토큰 변조 체크")
    void testLogoutMember_Failed_InvalidToken() throws Exception {
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

        doThrow(new InvalidEmailException("유효하지 않은 이메일 형식입니다."))
                .when(signupValidator).validateMember(memberRequest);
        // When
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> {
            authService.signupMember(memberRequest);
        });

        // Then
        assertEquals("유효하지 않은 이메일 형식입니다.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
        verify(signupValidator, times(1)).validateMember(memberRequest);
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

        doThrow(new IllegalArgumentException("닉네임 값을 입력해주세요."))
                .when(signupValidator).validateMember(memberRequest);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.signupMember(memberRequest);
        });

        // Then
        assertEquals("닉네임 값을 입력해주세요.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
        verify(signupValidator, times(1)).validateMember(memberRequest);
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

        doThrow(new IllegalArgumentException("비밀번호를 입력해주세요."))
                .when(signupValidator).validateMember(memberRequest);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.signupMember(memberRequest);
        });

        // Then
        assertEquals("비밀번호를 입력해주세요.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
        verify(signupValidator, times(1)).validateMember(memberRequest);
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

        doThrow(new DuplicateEmailException("이미 존재하는 이메일 입니다."))
                .when(signupValidator).validateMember(memberRequest);

        // When
        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class, () -> {
            authService.signupMember(memberRequest);
        });

        // Then
        assertEquals("이미 존재하는 이메일 입니다.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
        verify(signupValidator, times(1)).validateMember(memberRequest);

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

        doThrow(new DuplicateEmailException("이미 존재하는 닉네임 입니다."))
                .when(signupValidator).validateMember(memberRequest);

        // When
        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class, () -> {
            authService.signupMember(memberRequest);
        });

        // Then
        assertEquals("이미 존재하는 닉네임 입니다.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
        verify(signupValidator, times(1)).validateMember(memberRequest);
    }

    @Test
    @DisplayName("주소 공란 예외 처리")
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

        doThrow(new IllegalArgumentException("주소를 입력해주세요."))
                .when(signupValidator).validateMember(memberRequest);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.signupMember(memberRequest);
        });

        // Then
        assertEquals("주소를 입력해주세요.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
        verify(signupValidator, times(1)).validateMember(memberRequest);
    }

    @Test
    @DisplayName("상세 주소 공란 예외 처리")
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

        doThrow(new IllegalArgumentException("상세주소를 입력해주세요."))
                .when(signupValidator).validateMember(memberRequest);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.signupMember(memberRequest);
        });

        // Then
        assertEquals("상세주소를 입력해주세요.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
        verify(signupValidator, times(1)).validateMember(memberRequest);
    }

    @Test
    @DisplayName("우편번호 공란 예외 처리")
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

        doThrow(new IllegalArgumentException("우편번호를 입력해주세요."))
                .when(signupValidator).validateMember(memberRequest);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.signupMember(memberRequest);
        });

        // Then
        assertEquals("우편번호를 입력해주세요.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
        verify(signupValidator, times(1)).validateMember(memberRequest);
    }
}
