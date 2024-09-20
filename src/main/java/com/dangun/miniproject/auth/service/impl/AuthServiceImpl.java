package com.dangun.miniproject.auth.service.impl;

import com.dangun.miniproject.auth.jwt.JWTUtil;
import com.dangun.miniproject.auth.service.AuthService;
import com.dangun.miniproject.auth.service.validator.SignupValidator;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import com.dangun.miniproject.member.repository.MemberRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SignupValidator signupValidator;
    private final TokenBlackListService tokenBlackListService;
    private final JWTUtil jwtUtil;

    @Override
    public Member signupMember(GetMemberRequest memberReq) {
        signupValidator.validateMember(memberReq);
        Member member = memberReq.toEntity();
        member.updatePassword(bCryptPasswordEncoder.encode(member.getPassword()));

        memberRepository.save(member);
        return member;
    }

    @Override
    public void logoutMember(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String accessToken = authorizationHeader.substring(7);

        try {
            jwtUtil.isExpiredTokenAccess(accessToken);
            tokenBlackListService.addBlackListToken(accessToken);

            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setMaxAge(0);
            response.addCookie(refreshTokenCookie);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
