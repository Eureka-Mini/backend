package com.dangun.miniproject.controller;

import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public GetMemberRequest getMember(@PathVariable Long memberId) {
        try {
            return memberService.getMember(memberId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PutMapping("/{memberId}")
    public GetMemberRequest updateMember(
            @PathVariable Long memberId,
            @RequestBody GetMemberRequest getMemberRequest
    ){

        return memberService.updateMember(getMemberRequest, memberId);
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

