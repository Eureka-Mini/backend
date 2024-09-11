package com.dangun.miniproject.controller;

import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

