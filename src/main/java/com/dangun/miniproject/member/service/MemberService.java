package com.dangun.miniproject.member.service;

import com.dangun.miniproject.member.dto.GetAddressDto;
import com.dangun.miniproject.member.dto.GetMemberDto;
import org.springframework.http.ResponseEntity;

public interface MemberService {

    GetMemberDto getMember(Long id);

    GetMemberDto getMyInfo(Long id);

    GetMemberDto updateMember(GetMemberDto getMemberDto, Long id);

    GetAddressDto updateAddress(GetAddressDto getAddressDto, Long id);

    boolean deleteMember(Long id);
}
