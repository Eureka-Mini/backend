package com.dangun.miniproject.auth.service.impl;

import com.dangun.miniproject.auth.exception.exceptions.DuplicateEmailException;
import com.dangun.miniproject.auth.exception.exceptions.DuplicateNicknameException;
import com.dangun.miniproject.auth.exception.exceptions.InvalidEmailException;
import com.dangun.miniproject.auth.exception.exceptions.ReissueAccessTokenException;
import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.auth.service.AuthService;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import com.dangun.miniproject.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final static long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    public Member signupMember(GetMemberRequest memberReq) {
        validateMember(memberReq);
        Member member = memberReq.toEntity();
        member.updatePassword(bCryptPasswordEncoder.encode(member.getPassword()));

        memberRepository.save(member);
        return member;
    }

    @Override
    public void validateMember(GetMemberRequest memberReq) {
        validateEmailFormat(memberReq.getEmail());
        validatePassword(memberReq.getPassword());
        validateNickname(memberReq.getNickname());

        isConflictEmail(memberReq.getEmail());
        isConflictNickname(memberReq.getNickname());
    }

    private void validateEmailFormat(String email) {
        if (!isEmailValid(email)) {
            throw new InvalidEmailException("유효하지 않은 이메일 형식입니다.");
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임 값을 입력해주세요.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isBlank()) {
            throw new IllegalArgumentException("패스워드 값을 입력해주세요.");
        }
    }


    private void isConflictEmail(String email) {
        Boolean isExistEmail = memberRepository.existsByEmail(email);
        if (isExistEmail) {
            throw new DuplicateEmailException("이미 존재하는 이메일 입니다.");
        }
    }

    private void isConflictNickname(String nickname) {
        Boolean isExistNickname = memberRepository.existsByNickname(nickname);
        if (isExistNickname) {
            throw new DuplicateNicknameException("이미 존재하는 닉네임 입니다.");
        }
    }

    private boolean isEmailValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }


    // --------------------------------------- Token Logic

    @Override
    public String reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            throw new ReissueAccessTokenException("refreshToken null");
        }

        jwtUtil.isExpiredToken(refreshToken);

        String email = jwtUtil.getMemberEmail(refreshToken);
        String newAccessToken = jwtUtil.createJwt("accessToken", email, ACCESS_TOKEN_EXPIRE_TIME);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        return newAccessToken;
    }
}
