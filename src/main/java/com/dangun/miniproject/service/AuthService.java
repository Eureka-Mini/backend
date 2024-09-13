package com.dangun.miniproject.service;

import com.dangun.miniproject.dto.GetMemberRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> signupMember(GetMemberRequest member);

    ResponseEntity<?> reissueAccessToken(HttpServletRequest request, HttpServletResponse response);

}
