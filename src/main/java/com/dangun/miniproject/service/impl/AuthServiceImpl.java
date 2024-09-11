package com.dangun.miniproject.service.impl;

import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.repository.MemberRepository;
import com.dangun.miniproject.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void signupMember(GetMemberRequest memberReq) {
        String email = memberReq.getEmail();

        Boolean isMember = memberRepository.existsByEmail(email);

        if (isMember) {
            System.out.println("signupMember : 이미 존재하는 이메일입니다.");
            return;
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
    }
}
