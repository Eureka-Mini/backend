package com.dangun.miniproject.service;

import com.dangun.miniproject.dto.GetMemberRequest;

public interface AuthService {

    boolean signupMember(GetMemberRequest member);
}
