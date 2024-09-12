package com.dangun.miniproject.controller;

import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.dto.UserDetailsDto;
import com.dangun.miniproject.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signupMember(@RequestBody GetMemberRequest member) {
       boolean result = authService.signupMember(member);

       if (!result) {
           return ResponseEntity.status(HttpStatus.CONFLICT).body("이메일 요청이 올바르지 않습니다.");
       }

        return ResponseEntity.ok("Sign up Success!");
    }

    @GetMapping("/jwt/test")
    public String test(@AuthenticationPrincipal UserDetailsDto userDetailsDto) {
        Member member = userDetailsDto.getMember();
        System.out.println(member.getNickname());
        System.out.println(userDetailsDto.getUsername());
        return "검증 완료";
    }
}
