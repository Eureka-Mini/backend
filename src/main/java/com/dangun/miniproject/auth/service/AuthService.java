package com.dangun.miniproject.auth.service;

import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    Member signupMember(GetMemberRequest member);

    String reissueAccessToken(HttpServletRequest request, HttpServletResponse response);

    void validateMember(GetMemberRequest memberReq);
}
