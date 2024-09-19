package com.dangun.miniproject.auth.service.impl;

import com.dangun.miniproject.auth.service.AuthService;
import com.dangun.miniproject.auth.service.validator.SignupValidator;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import com.dangun.miniproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SignupValidator signupValidator;

    @Override
    public Member signupMember(GetMemberRequest memberReq) {
        signupValidator.validateMember(memberReq);
        Member member = memberReq.toEntity();
        member.updatePassword(bCryptPasswordEncoder.encode(member.getPassword()));

        memberRepository.save(member);
        return member;
    }
}
