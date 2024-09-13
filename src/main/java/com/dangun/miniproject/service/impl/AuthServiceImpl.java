package com.dangun.miniproject.service.impl;

import com.dangun.miniproject.common.ApiResponse;
import com.dangun.miniproject.domain.Address;
import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.jwt.JWTUtil;
import com.dangun.miniproject.repository.MemberRepository;
import com.dangun.miniproject.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final static long ACCESS_TOKEN_EXPIRE_TIME = 60 * 10 * 1000L;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    public ResponseEntity<?> signupMember(GetMemberRequest memberReq) {
        ResponseEntity<?> validated = validateSignup(memberReq.getNickname(), memberReq.getPassword(), memberReq.getEmail());

        if (!validated.getStatusCode().is2xxSuccessful()) {
            return validated;
        }

        String encodedPassword = bCryptPasswordEncoder.encode(memberReq.getPassword());

        Member member = Member.builder()
                .email(memberReq.getEmail())
                .nickname(memberReq.getNickname())
                .password(encodedPassword)
                .build();

        Address address = Address.builder()
                .detail(memberReq.getAddress().getDetail())
                .street(memberReq.getAddress().getStreet())
                .zipcode(memberReq.getAddress().getZipcode())
                .member(member)
                .build();

        member.addAddress(address);
        memberRepository.save(member);

        Map<String, String> data = new HashMap<>();
        data.put("nickname", member.getNickname());

        return ApiResponse.ok("SING-UP-S001", data, "Sign up Success");
    }

    private ResponseEntity<?> validateSignup(String nickname, String password, String email) {
        if (!isEmailValid(email)) {
            return  ApiResponse.badRequest("MEMBER-F004", "it's not email");
        }

        if (password == null || password.trim().isEmpty()) {
            return ApiResponse.badRequest("MEMBER-F001", "password empty");
        }

        if (nickname == null || nickname.trim().isEmpty()) {
            return ApiResponse.badRequest("MEMBER-F002", "nickname empty");
        }

        Boolean isExistEmail = memberRepository.existsByEmail(email);
        if (isExistEmail) {
            return  ApiResponse.badRequest("MEMBER-F005", "already email exist");
        }

        Boolean isExistNickname = memberRepository.existsByNickname(nickname);
        if (isExistNickname) {
            return ApiResponse.badRequest("MEMBER-F003", "nickname overlap");
        }

        return ApiResponse.ok("success", null, "success");
    }

    private boolean isEmailValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }



    // --------------------------------------- Token Logic
    @Override
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ApiResponse.badRequest("AUTH_F001", "refreshToken null");
        }

        try {
            jwtUtil.isExpiredToken(refreshToken);
        } catch (ExpiredJwtException e) {
            return ApiResponse.badRequest("AUTH_F002", "refreshToken expired");
        }

        String category = jwtUtil.getJwtCategory(refreshToken);

        if (!category.equals("refreshToken")) {
            return ApiResponse.badRequest("AUTH_F002", "not refreshToken");
        }

        String email = jwtUtil.getMemberEmail(refreshToken);
        String newAccessToken = jwtUtil.createJwt("accessToken", email, ACCESS_TOKEN_EXPIRE_TIME);

        response.setHeader("accessToken", newAccessToken);

        Map<String, String> data = new HashMap<>();
        data.put("accessToken", newAccessToken);

        return ApiResponse.ok("AUTH_S001", data, "accessToken reissue success!!");
    }
}
