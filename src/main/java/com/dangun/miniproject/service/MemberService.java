package com.dangun.miniproject.service;

import com.dangun.miniproject.dto.GetMemberRequest;

public interface MemberService {

    GetMemberRequest getMember(Long id);

    GetMemberRequest updateMember(GetMemberRequest getMemberRequest, Long id);

    boolean deleteMember(Long id);
}
