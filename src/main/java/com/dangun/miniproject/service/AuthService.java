package com.dangun.miniproject.service;

import com.dangun.miniproject.dto.GetMemberRequest;

public interface AuthService {

    void signupMember(GetMemberRequest member);
}
