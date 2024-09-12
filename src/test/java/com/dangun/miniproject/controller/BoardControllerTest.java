package com.dangun.miniproject.controller;

import com.dangun.miniproject.domain.BoardStatus;
import com.dangun.miniproject.dto.BoardResponse;
import com.dangun.miniproject.dto.CreateBoardRequest;
import com.dangun.miniproject.dto.UpdateBoardRequest;
import com.dangun.miniproject.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); // @Mock 어노테이션으로 생성된 모의 객체를 초기화
    }


    //게시글 생성 테스트
    @Test
    @WithMockUser // 가상의 인증된 사용자를 생성
    public void testCreateBoard() throws Exception {
        //43번으로 추가
        CreateBoardRequest request = new CreateBoardRequest(
                "테스트",
                "테스트내용",
                1000,
                BoardStatus.판매중,
                1L
        );
        BoardResponse response = new BoardResponse(
                43L,
                "테스트",
                "테스트내용",
                1000,
                BoardStatus.판매중,
                1L
        );

        // Mockito 설정
        when(boardService.createBoard(request)).thenReturn(response);

        // 요청 및 검증 ( JSON 응답 확인 )
        mockMvc.perform(MockMvcRequestBuilders.post("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())  // 응답 상태 코드가 201 Created인지 확인
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("테스트"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("테스트내용"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.boardStatus").value("판매중"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value(1));
    }




    // 게시글 수정 테스트
    @Test
    @WithMockUser
    public void testUpdateBoard() throws Exception {
        // 41번 글 수정
        Long boardId = 41L;
        UpdateBoardRequest request = new UpdateBoardRequest(
                "테스트(판매완료)",
                "판매완료했어요",
                1234,
                BoardStatus.판매완료
        );
        BoardResponse response = new BoardResponse(
                boardId,
                "테스트(판매완료)",
                "판매완료했어요",
                1234, // 응답에서 price는 1234로 설정
                BoardStatus.판매완료,
                6L
        );

        // Mockito 설정
        when(boardService.updateBoard(boardId, request)).thenReturn(response);

        // 요청 및 검증
        mockMvc.perform(MockMvcRequestBuilders.put("/boards/{boardId}", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("테스트(판매완료)"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("판매완료했어요"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(1234))
                .andExpect(MockMvcResultMatchers.jsonPath("$.boardStatus").value("판매완료"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value(6));
    }




    // 게시글 삭제 테스트
    @Test
    @WithMockUser
    public void testDeleteBoard() throws Exception {
        // 42번 삭제
        Long boardId = 42L;

        // Mockito 설정
        doNothing().when(boardService).deleteBoard(boardId);

        // 요청 및 검증
        mockMvc.perform(MockMvcRequestBuilders.delete("/boards/{boardId}", boardId))
                .andExpect(MockMvcResultMatchers.status().isNoContent()); // 삭제 성공 시 상태 코드 204 No Content
    }

}
