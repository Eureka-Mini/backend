package com.dangun.miniproject.member.service;

import com.dangun.miniproject.member.dto.GetMemberDto;
import org.springframework.http.ResponseEntity;

public interface MemberService {

    GetMemberDto getMember(Long id);

    GetMemberDto getMyInfo(Long id);

    ResponseEntity<GetMemberDto> updateMember(GetMemberDto getMemberDto, Long id);

    boolean deleteMember(Long id);
}
