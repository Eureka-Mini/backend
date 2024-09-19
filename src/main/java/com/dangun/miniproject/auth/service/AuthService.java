package com.dangun.miniproject.auth.service;

import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberRequest;

public interface AuthService {

    Member signupMember(GetMemberRequest member);
}
