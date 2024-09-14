package com.dangun.miniproject.service;

import com.dangun.miniproject.dto.GetMemberDto;
import com.dangun.miniproject.dto.GetMemberResponse;
import org.springframework.http.ResponseEntity;

public interface MemberService {

    GetMemberDto getMember(Long id);

    GetMemberDto getMyInfo(Long id);

    ResponseEntity<GetMemberDto> updateMember(GetMemberDto getMemberDto, Long id);

    boolean deleteMember(Long id);
}
