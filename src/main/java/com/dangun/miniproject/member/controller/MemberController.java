package com.dangun.miniproject.member.controller;

import com.dangun.miniproject.common.ApiResponse;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetAddressDto;
import com.dangun.miniproject.member.dto.GetMemberDto;
import com.dangun.miniproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 다른 회원들의 정보 조회 가능
    @GetMapping("/{memberId}")
    public ResponseEntity<?> getMember(@PathVariable Long memberId) {
        GetMemberDto getMemberDto = memberService.getMember(memberId);


        return ApiResponse.ok("MEMBER-S002", getMemberDto, "회원 정보 조회 성공");
    }

    // 로그인 한 사용자만 자신의 정보만 조회 가능
    @GetMapping("/my-info")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal(expression = "member") Member member) {
        GetMemberDto getMemberDto = memberService.getMyInfo(member.getId());

        // 정상 처리
        return ApiResponse.ok("MEMBER-S002", getMemberDto, "회원 정보 조회 성공");
    }


    // 회원 정보 수정
    @PutMapping("my-info-update")
    public ResponseEntity<?> updateMember(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestBody GetMemberDto getMemberDto
    ) {
        GetMemberDto updateMember = memberService.updateMember(getMemberDto, member.getId());

        return ApiResponse.ok("MEMBER-S003",updateMember,"회원 정보 수정 성공");
    }

    // 회원 정보 수정
    @PutMapping("my-address-update")
    public ResponseEntity<?> updateAddress(
            @AuthenticationPrincipal(expression = "member") Member address,
            @RequestBody GetAddressDto getAddressDto
    ) {
        GetAddressDto updateAddress = memberService.updateAddress(getAddressDto, address.getId());

        return ApiResponse.ok("MEMBER-S003",updateAddress,"회원의 주소 정보 수정 성공");
    }


    @DeleteMapping("/my-info-delete")
    public ResponseEntity<?> deleteMember(
            @AuthenticationPrincipal(expression = "member") Member member) {
        boolean isDeleted = memberService.deleteMember(member.getId());

        return ApiResponse.ok("MEMBER-S004", null, "회원 탈퇴 성공");
    }
}

