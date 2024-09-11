package com.dangun.miniproject.service;

import com.dangun.miniproject.dto.GetMemberRequest;

public interface MemberService {

    GetMemberRequest getMember(Long id);
}
