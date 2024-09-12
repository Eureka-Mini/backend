package com.dangun.miniproject.controller;

import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.UpdateCommentRequest;
import com.dangun.miniproject.dto.UpdateCommentResponse;
import com.dangun.miniproject.dto.WriteCommentRequest;
import com.dangun.miniproject.dto.WriteCommentResponse;
import com.dangun.miniproject.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Nested
    class writeComment {

        @Test
        void 댓글_작성_성공_200() throws Exception {
            // given
            Long boardId = 1L;
            WriteCommentRequest request = mock(WriteCommentRequest.class);
            WriteCommentResponse response = new WriteCommentResponse("테스트댓글");
            Member member = mock(Member.class);

            when(request.getContent()).thenReturn("테스트댓글");
            when(commentService.writeComment(any(), any(Long.class), any(WriteCommentRequest.class)))
                    .thenReturn(response);

            // when
            ResultActions result = mockMvc.perform(post("/boards/{boardId}/comments", boardId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(authentication(new TestingAuthenticationToken(member, null, AuthorityUtils.createAuthorityList("ROLE_USER"))))
                    .with(csrf())
                    .content(new ObjectMapper().writeValueAsString(request)));

            // then
            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.content").value("테스트댓글"))
                    .andExpect(jsonPath("$.message").value("Create Success"))
                    .andDo(print());

            verify(commentService).writeComment(any(Member.class), eq(boardId), any(WriteCommentRequest.class));
        }

        @Test
        void 내용이_존재하지_않는_댓글_작성_시도_400() throws Exception {
            // given
            Long boardId = 1L;
            WriteCommentRequest request = mock(WriteCommentRequest.class);
            Member member = mock(Member.class);

            when(request.getContent()).thenReturn("");

            // when
            ResultActions result = mockMvc.perform(post("/boards/{boardId}/comments", boardId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(authentication(new TestingAuthenticationToken(member, null, AuthorityUtils.createAuthorityList("ROLE_USER"))))
                    .with(csrf())
                    .content(new ObjectMapper().writeValueAsString(request)));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Content is blank"));
        }
    }

    @Nested
    class updateComment {

        @Test
        void 댓글_수정_성공_200() throws Exception {
            // given
            Long boardId = 1L;
            Long commentId = 2L;
            UpdateCommentRequest request = mock(UpdateCommentRequest.class);
            UpdateCommentResponse response = new UpdateCommentResponse("테스트댓글 수정");
            Member member = mock(Member.class);

            when(request.getContent()).thenReturn("테스트댓글 수정");
            when(commentService.updateComment(eq(boardId), eq(commentId), any(Member.class), any(UpdateCommentRequest.class)))
                    .thenReturn(response);

            // when
            ResultActions result = mockMvc.perform(put("/boards/{boardId}/comments/{commentId}", boardId, commentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .with(authentication(new TestingAuthenticationToken(member, null, AuthorityUtils.createAuthorityList("ROLE_USER"))))
                    .with(csrf())
                    .content(new ObjectMapper().writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value("테스트댓글 수정"))
                    .andExpect(jsonPath("$.message").value("Update Success"))
                    .andDo(print());

            ArgumentCaptor<UpdateCommentRequest> requestCaptor = ArgumentCaptor.forClass(UpdateCommentRequest.class);
            verify(commentService).updateComment(eq(boardId), eq(commentId), eq(member), requestCaptor.capture());

            UpdateCommentRequest capturedRequest = requestCaptor.getValue();
            assertThat("테스트댓글 수정").isEqualTo(capturedRequest.getContent());
        }

    }
}