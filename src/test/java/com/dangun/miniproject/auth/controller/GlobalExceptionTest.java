package com.dangun.miniproject.auth.controller;

import com.dangun.miniproject.auth.exception.exceptions.DuplicateEmailException;
import com.dangun.miniproject.auth.exception.exceptions.DuplicateNicknameException;
import com.dangun.miniproject.auth.exception.exceptions.InvalidEmailException;
import com.dangun.miniproject.member.dto.GetAddressRequest;
import com.dangun.miniproject.member.dto.GetMemberRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthController authController;

    @Test
    @DisplayName("testHandleInvalidEmailException 처리")
    void testHandleInvalidEmailException() throws Exception {
        // Given
        String invalidEmail = "invalid-email";
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email(invalidEmail)
                .nickname("nickname")
                .password("password")
                .address(GetAddressRequest.builder()
                        .street("street")
                        .detail("detail")
                        .zipcode("zipcode")
                        .build())
                .build();

        doThrow(new InvalidEmailException("유효하지 않은 이메일 형식입니다."))
                .when(authController).signupMember(any(GetMemberRequest.class));

        // When & Then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효하지 않은 이메일 형식입니다."))
                .andExpect(jsonPath("$.code").value("MEMBER-F000"));
    }

    @Test
    @DisplayName("HandleIllegalArgumentException 처리")
    void testHandleIllegalArgumentException() throws Exception {
        // Given
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email("test@test.com")
                .nickname("")
                .password("password")
                .address(GetAddressRequest.builder()
                        .street("street")
                        .detail("detail")
                        .zipcode("zipcode")
                        .build())
                .build();

        doThrow(new IllegalArgumentException("닉네임 값을 입력해주세요."))
                .when(authController).signupMember(any(GetMemberRequest.class));

        // When & Then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("닉네임 값을 입력해주세요."))
                .andExpect(jsonPath("$.code").value("MEMBER-F000"));
    }

    @Test
    @DisplayName("HandleDuplicateEmailException 처리")
    void testHandleDuplicateEmailException() throws Exception {
        // Given
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email("test@test.com")
                .nickname("nickname")
                .password("password")
                .address(GetAddressRequest.builder()
                        .street("street")
                        .detail("detail")
                        .zipcode("zipcode")
                        .build())
                .build();

        doThrow(new DuplicateEmailException("이미 존재하는 이메일 입니다."))
                .when(authController).signupMember(any(GetMemberRequest.class));

        // When & Then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 이메일 입니다."))
                .andExpect(jsonPath("$.code").value("MEMBER-F009"));
    }

    @Test
    @DisplayName("HandleDuplicateNicknameException 처리")
    void testHandleDuplicateNicknameException() throws Exception {
        // Given
        GetMemberRequest memberRequest = GetMemberRequest.builder()
                .email("test@test.com")
                .nickname("nickname")
                .password("password")
                .address(GetAddressRequest.builder()
                        .street("street")
                        .detail("detail")
                        .zipcode("zipcode")
                        .build())
                .build();

        doThrow(new DuplicateNicknameException("이미 존재하는 닉네임 입니다."))
                .when(authController).signupMember(any(GetMemberRequest.class));

        // When & Then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 닉네임 입니다."))
                .andExpect(jsonPath("$.code").value("MEMBER-F009"));
    }
}
