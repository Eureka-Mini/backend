package com.dangun.miniproject.auth.controller;

import com.dangun.miniproject.auth.service.AuthService;
import com.dangun.miniproject.common.ApiResponse;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signupMember(@RequestBody GetMemberRequest memberReq) {
        Member member = authService.signupMember(memberReq);

        Map<String, String> data = new HashMap<>();
        data.put("nickname", member.getNickname());

        return ApiResponse.ok("SING-UP-S001", data, "Sign up Success");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutMember(HttpServletRequest request, HttpServletResponse response) {
        authService.logoutMember(request, response);

        Map<String, String> data = new HashMap<>();
        data.put("result", "set token blacklist / cookie delete");

        return ApiResponse.ok("AUTH_S002", data,"logout Success");
    }
}
