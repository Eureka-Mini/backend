package com.dangun.miniproject.member.controller;

import com.dangun.miniproject.common.ApiResponse;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberDto;
import com.dangun.miniproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public GetMemberDto getMember(@PathVariable Long memberId) {
        return memberService.getMember(memberId);
    }

    // 로그인 한 사용자만 자신의 정보만 조회 가능
    @GetMapping("/my-info")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal(expression = "member") Member member) {
        GetMemberDto getMemberDto = memberService.getMyInfo(member.getId());

        // 정상 처리
        return ApiResponse.ok("MEMBER-S002", getMemberDto, "회원 정보 조회 성공");
    }


    // 회원 정보 수정
    @PutMapping("/{memberId}")
    public ResponseEntity<GetMemberDto> updateMember(
            @PathVariable Long memberId,
            @RequestBody GetMemberDto getMemberDto
    ) {
        return memberService.updateMember(getMemberDto, memberId);
    }


    @DeleteMapping("/{memberId}")
    public ResponseEntity<String> deleteMember(@PathVariable Long memberId) {
        boolean isDeleted = memberService.deleteMember(memberId);

        if (isDeleted) {
            return ResponseEntity.ok("Member deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found.");
        }
    }
}

