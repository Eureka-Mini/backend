package com.dangun.miniproject.service;

import com.dangun.miniproject.dto.GetMemberRequest;
import org.springframework.http.ResponseEntity;

public interface MemberService {

    GetMemberRequest getMember(Long id);

    ResponseEntity<GetMemberRequest> updateMember(GetMemberRequest getMemberRequest, Long id);

    boolean deleteMember(Long id);
}
