package com.dangun.miniproject.controller;


import com.dangun.miniproject.dto.GetAddressRequest;
import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
        GetAddressRequest mockAddress = new GetAddressRequest(1L, "123 주요 거리", "101동 아파트", "14352");
        GetMemberRequest mockResponse = new GetMemberRequest(1L, "Hong@test.com", "1234", "Hong", mockAddress);

        // when: MemberService의 getMember가 호출될 때, mockResponse를 반환하도록 설정
        when(memberService.getMember(anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(get("/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("Hong@test.com"))
                .andExpect(jsonPath("$.nickname").value("Hong"))
                .andExpect(jsonPath("$.address.id").value(1L))
                .andExpect(jsonPath("$.address.street").value("123 주요 거리"))
                .andExpect(jsonPath("$.address.detail").value("101동 아파트"))
                .andExpect(jsonPath("$.address.zipcode").value("14352"));
    }

    @Test
    @WithMockUser(username = "user")
    void updateMember() throws Exception {
        // given
        GetAddressRequest mockAddress = new GetAddressRequest(4L, "부산광역시 해운대구 해운대로 620", "4동 203호", "48093");
        GetMemberRequest updateRequest = new GetMemberRequest(4L, "minah@naver.com", "password4", "minah", mockAddress);
        GetMemberRequest updatedResponse = new GetMemberRequest(4L, "minah@naver.com", "password4", "minah", mockAddress);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(4L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("minah@naver.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("minah"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.id").value(4L));
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