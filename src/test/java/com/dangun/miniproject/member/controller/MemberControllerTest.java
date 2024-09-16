package com.dangun.miniproject.member.controller;


import com.dangun.miniproject.auth.dto.UserDetailsDto;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetAddressDto;
import com.dangun.miniproject.member.dto.GetMemberDto;
import com.dangun.miniproject.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(username = "user")
    void getMember() throws Exception {
        // given: GetAddressRequest 객체 생성, GetMemberRequest 객체 생성
        GetAddressDto mockAddress = new GetAddressDto("testStreet", "testDetail", "12345");
        GetMemberDto mockResponse = new GetMemberDto("test@test.com", "testUser", mockAddress);

        // when: MemberService의 getMember가 호출될 때, mockResponse를 반환하도록 설정
        when(memberService.getMember(anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(get("/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.nickname").value("testUser"))
                .andExpect(jsonPath("$.address.street").value("testStreet"))
                .andExpect(jsonPath("$.address.detail").value("testDetail"))
                .andExpect(jsonPath("$.address.zipcode").value("12345"));
    }

    @Test
    void myInfoWhenLoggedIn() throws Exception {
        // given: Member 정보 설정
        GetAddressDto mockAddress = new GetAddressDto("testStreet", "testDetail", "12345");
        GetMemberDto mockResponse = new GetMemberDto("test@example.com", "tester", mockAddress);
        Member member = new Member(); // Member 객체 초기화 필요
        UserDetails userDetails = new UserDetailsDto(member);

        // memberService.getMyInfo 호출 시 mockResponse 반환하도록 설정
        when(memberService.getMyInfo(any(Long.class))).thenReturn(mockResponse);

        // mockResponse의 값이 제대로 설정되었는지 확인
        System.out.println("mockResponse: " + mockResponse);

        // when & then: 응답 검증
        mockMvc.perform(MockMvcRequestBuilders.get("/members/my-info")
                        .accept(APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .with(authentication(UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities())))
                        .with(csrf()))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "user")
    void updateMember() throws Exception {
        // given
        GetAddressDto mockAddress = new GetAddressDto("부산광역시 해운대구 해운대로 620", "4동 203호", "48093");
        GetMemberDto updateRequest = new GetMemberDto("minah@naver.com", "minah", mockAddress);
        GetMemberDto updatedResponse = new GetMemberDto("minah@naver.com", "minah", mockAddress);

        // Mockito 설정
        when(memberService.updateMember(Mockito.any(), Mockito.eq(4L)))
                .thenReturn(ResponseEntity.ok(updatedResponse));

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/members/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateRequest))
                .with(csrf()));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("minah@naver.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("minah"));
    }

    @Test
    @WithMockUser(username = "user")
    public void deleteMember() throws Exception {
        // given
        Long memberId = 1L;
        when(memberService.deleteMember(memberId)).thenReturn(true);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/members/{memberId}", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        // then
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Member deleted successfully."));

        verify(memberService).deleteMember(memberId); // deleteMember 메서드가 호출됐는지 검증
    }


}