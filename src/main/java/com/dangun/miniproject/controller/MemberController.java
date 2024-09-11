package com.dangun.miniproject.controller;

import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.service.MemberService;
import jakarta.servlet.http.HttpSession;
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
    public GetMemberRequest getMember(@PathVariable Long memberId, HttpSession session) {
        try {
            // 세션에 memberId를 저장
            session.setAttribute("memberId", memberId);

            // 이후 세션에서 memberId를 가져옴
            Long id = (Long) session.getAttribute("memberId");

            // 서비스 로직 호출
            return memberService.getMember(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PutMapping("/{memberId}")
    public GetMemberRequest updateMember(
            @PathVariable Long memberId,
            @RequestBody GetMemberRequest getMemberRequest,
            HttpSession session
    ){
        // 세션에 memberId를 저장
        session.setAttribute("memberId", memberId);

        // 이후 세션에서 memberId를 가져옴
        Long id = (Long) session.getAttribute("memberId");

        // 서비스 로직 호출
        return memberService.updateMember(getMemberRequest, id);
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

