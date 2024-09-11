package com.dangun.miniproject.controller;

import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.service.AuthService;
import lombok.RequiredArgsConstructor;
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
    public String signupMember(@RequestBody GetMemberRequest member) {
       authService.signupMember(member);
       return "Signup OK";
    }
}
