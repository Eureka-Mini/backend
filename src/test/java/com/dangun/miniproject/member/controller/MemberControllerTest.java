package com.dangun.miniproject.member.controller;


import com.dangun.miniproject.auth.dto.UserDetailsDto;
import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetAddressDto;
import com.dangun.miniproject.member.dto.GetMemberDto;
import com.dangun.miniproject.member.exception.AddressNotFoundException;
import com.dangun.miniproject.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        CodeKey mockCodeKey = new CodeKey("020", "010");
        GetMemberDto mockResponse = new GetMemberDto("test@test.com", "testUser", mockAddress, mockCodeKey);

        // when: MemberService의 getMember가 호출될 때, mockResponse를 반환하도록 설정
        when(memberService.getMember(anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(get("/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.nickname").value("testUser"))
                .andExpect(jsonPath("$.data.address.street").value("testStreet"))
                .andExpect(jsonPath("$.data.address.detail").value("testDetail"))
                .andExpect(jsonPath("$.data.address.zipcode").value("12345"))
                .andExpect(jsonPath("$.data.codeKey.groupCode").value("020"))
                .andExpect(jsonPath("$.data.codeKey.code").value("010"));
    }

    @Test
    void myInfoWhenLoggedIn() throws Exception {
        // given: Member 정보 설정
        GetAddressDto mockAddress = new GetAddressDto("testStreet", "testDetail", "12345");
        CodeKey mockCodeKey = new CodeKey("020", "010");
        GetMemberDto mockResponse = new GetMemberDto("test@example.com", "tester", mockAddress, mockCodeKey);

        Member member = new Member();
        setField(member, "id", 1L);
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("tester"))
                .andExpect(jsonPath("$.data.address.street").value("testStreet"))
                .andExpect(jsonPath("$.data.address.detail").value("testDetail"))
                .andExpect(jsonPath("$.data.address.zipcode").value("12345"))
                .andExpect(jsonPath("$.data.codeKey.groupCode").value("020"))
                .andExpect(jsonPath("$.data.codeKey.code").value("010"));
    }


    @Test
    void UpdateMemberWhenLoggedIn() throws Exception {
        // Given: 테스트에 사용할 DTO 데이터 정의
        GetAddressDto mockAddress = new GetAddressDto("street", "detail", "11111");
        CodeKey mockCodeKey = new CodeKey("010", "020");
        given(memberService.updateMember(any(), anyLong()))
                .willReturn(new GetMemberDto("newMinah@naver.com", "newMinah", mockAddress, mockCodeKey));

        Member member = new Member();
        setField(member, "id", 4L);
        UserDetails userDetails = new UserDetailsDto(member);

        // Mock: memberService에서 사용될 메서드의 동작을 가짜로 정의
        GetMemberDto updatedMemberDto = GetMemberDto.builder()
                .email("newMinah@naver.com")
                .nickname("newMinah")
                .build();

        // When: PUT 요청을 MockMvc를 사용하여 전송
        mockMvc.perform(put("/members/my-info-update")
                        .accept(APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMemberDto))  // 수정된 회원 정보를 전송
                        .characterEncoding(StandardCharsets.UTF_8)
                        .with(authentication(UsernamePasswordAuthenticationToken.authenticated(userDetails, updatedMemberDto, userDetails.getAuthorities()))).with(csrf())

                )
                // Then: 응답 상태와 JSON 필드 검증
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("MEMBER-S003"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원 정보 수정 성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("newMinah"));
    }


    @Test
    void UpdateAddressWhenLoggedIn() throws Exception {
        // Given: 테스트에 사용할 DTO 데이터 정의
        given(memberService.updateAddress(any(), anyLong()))
                .willReturn(new GetAddressDto("newStreet", "newDetail", "00000"));

        Member member = new Member();
        setField(member, "id", 1L);
        UserDetails userDetails = new UserDetailsDto(member);

        // Mock: memberService에서 사용될 메서드의 동작을 가짜로 정의
        GetAddressDto updatedAddressDto = GetAddressDto.builder()
                .street("newStreet")
                .detail("newDetail")
                .zipcode("00000")
                .build();

        // When: PUT 요청을 MockMvc를 사용하여 전송
        mockMvc.perform(put("/members/my-address-update")
                        .accept(APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAddressDto))  // 수정된 회원 정보를 전송
                        .characterEncoding(StandardCharsets.UTF_8)
                        .with(authentication(UsernamePasswordAuthenticationToken.authenticated(userDetails, updatedAddressDto, userDetails.getAuthorities()))).with(csrf())

                )
                // Then: 응답 상태와 JSON 필드 검증
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("MEMBER-S003"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원의 주소 정보 수정 성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.street").value("newStreet"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.detail").value("newDetail"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.zipcode").value("00000"));
    }

    @Test
    void deleteMemberWhenLoggedIn() throws Exception {
        // Given
        Member member = new Member();
        setField(member, "id", 1L);
        UserDetails userDetails = new UserDetailsDto(member);

        GetMemberDto updatedMemberDto = GetMemberDto.builder()
                .email("test@test.com")
                .nickname("tester")
                .build();

        when(memberService.deleteMember(member.getId())).thenReturn(true);

        // When & Then: DELETE 요청을 MockMvc를 사용하여 전송
        mockMvc.perform(delete("/members/my-info-delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(UsernamePasswordAuthenticationToken.authenticated(userDetails, updatedMemberDto, userDetails.getAuthorities()))).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("MEMBER-S004"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원 탈퇴 성공"));
    }

    @Test
    void 회원_주소_변경_실패_찾을_수_없는_주소() throws Exception {
        // given
        Member member = new Member();
        setField(member, "id", 1L);
        UserDetails userDetails = new UserDetailsDto(member);

        Mockito.when(memberService.updateAddress(any(GetAddressDto.class), anyLong()))
                .thenThrow(new AddressNotFoundException());

        // When & Then
        mockMvc.perform(put("/members/my-address-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"street\": \"st\", \"detail\": \"detail\", \"zipcode\": \"12345\" }")
                        .with(csrf())
                        .with(authentication(UsernamePasswordAuthenticationToken.authenticated(userDetails, null, null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MEMBER-F101"))
                .andExpect(jsonPath("$.message").value("Address not found"));

    }
}