package com.dangun.miniproject.auth.controller;

import com.dangun.miniproject.auth.service.impl.AuthServiceImpl;
import com.dangun.miniproject.fixture.AddressFixture;
import com.dangun.miniproject.fixture.MemberFixture;
import com.dangun.miniproject.member.domain.Address;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthServiceImpl authService;

    @Test
    @DisplayName("회원 가입 성공 테스트")
    void testSignupMember_Success() throws Exception {
        // Given
        GetMemberRequest memberRequest = MemberFixture.instanceOf();
        Member member = memberRequest.toEntity();

        Address address = AddressFixture.instanceOf(member);
        member.addAddress(address);

        Map<String, String> data = new HashMap<>();
        data.put("nickname", member.getNickname());

        when(authService.signupMember(any(GetMemberRequest.class))).thenReturn(member);

        // When && Then
        mockMvc.perform(post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(memberRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SING-UP-S001"))
                .andExpect(jsonPath("$.message").value("Sign up Success"))
                .andExpect(jsonPath("$.data.nickname").value(member.getNickname()));

        verify(authService, times(1)).signupMember(any(GetMemberRequest.class));
    }

    @Test
    @DisplayName("로그 아웃 성공 테스트")
    void testLogoutMember_Success() throws Exception {
        // Given
        String accessToken = "accessToken";
        Map<String, String> data = new HashMap<>();
        data.put("result", "set token blacklist / cookie delete");

        // When && Then
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("AUTH_S002"))
                .andExpect(jsonPath("$.message").value("logout Success"))
                .andExpect(jsonPath("$.data.result").value("set token blacklist / cookie delete"));

        verify(authService, times(1)).logoutMember(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}
