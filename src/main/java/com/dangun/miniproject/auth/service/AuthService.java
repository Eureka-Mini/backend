package com.dangun.miniproject.auth.service;

import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    Member signupMember(GetMemberRequest member);

    void logoutMember(HttpServletRequest request, HttpServletResponse response);
}
