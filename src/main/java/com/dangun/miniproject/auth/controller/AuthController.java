package com.dangun.miniproject.auth.controller;

import com.dangun.miniproject.common.ApiResponse;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import com.dangun.miniproject.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signupMember(@RequestBody GetMemberRequest member) {

        try{
            authService.signupMember(member);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("", e.getMessage()));
        }

        return authService.signupMember(member);
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.reissueAccessToken(request, response);
    }
}
