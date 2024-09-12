package com.dangun.miniproject;


import com.dangun.miniproject.controller.MemberController;
import com.dangun.miniproject.dto.GetAddressRequest;
import com.dangun.miniproject.dto.GetMemberRequest;
import com.dangun.miniproject.service.MemberService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getMember() throws Exception {
        // given: GetAddressRequest 객체 생성, GetMemberRequest 객체 생성
        GetAddressRequest mockAddress = new GetAddressRequest(1L, "123 주요 거리", "101동 아파트", "14352");
        GetMemberRequest mockResponse = new GetMemberRequest(1L, "Hong@test.com", "1234", "Hong", mockAddress);

        // when: MemberService의 getMember가 호출될 때, mockResponse를 반환하도록 설정
        Mockito.when(memberService.getMember(anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(get("/members/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("Hong@test.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("Hong"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.street").value("123 주요 거리"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.detail").value("101동 아파트"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address.zipcode").value("14352"));
    }

}