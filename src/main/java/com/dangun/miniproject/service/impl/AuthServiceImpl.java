package com.dangun.miniproject.service.impl;

import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.repository.MemberRepository;
import com.dangun.miniproject.service.AuthService;
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

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    public boolean signupMember(GetMemberRequest memberReq) {
        String email = memberReq.getEmail();

        if (!isEmailValid(email)) {
            log.warn("올바른 이메일 형식을 입력해주세요.");
            return false;
        }

        Boolean isMember = memberRepository.existsByEmail(email);

        if (isMember) {
            log.warn("이미 존재하는 이메일입니다.");
            return false;
        }

        String rawPassword = memberReq.getPassword();

        // 비밀번호 Validate
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        // encoded
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);

        Member member = Member.builder()
                .email(memberReq.getEmail())
                .nickname(memberReq.getNickname())
                .password(encodedPassword)
                .build();

        memberRepository.save(member);
        return true;
    }

    private boolean isEmailValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
