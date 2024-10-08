package com.dangun.miniproject.comment.controller;

import com.dangun.miniproject.comment.dto.*;
import com.dangun.miniproject.comment.service.CommentService;
import com.dangun.miniproject.common.ApiResponse;
import com.dangun.miniproject.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @Project : Oyee market
 * @package_name        : com.dangun.miniproject.comment.controller
 * @FileName : CommentController
 * @since : 2024. 09. 16
 * @version 1.0
 * @author : 김영철
 * @프로그램 설명 : Comment 도메인 컨트롤러 클래스
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일               수정자            수정내용
 *  ----------   --------   ---------------------------
 *  2023.09.12       김영철         최초생성
 *  2023.09.16       김영철         패키지 구조 변경
 *  </pre>
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * @Method Name : writeComment
     * @date : 2024. 09. 12.
     * @Method 설명 :
     * <p>
     * 특정 게시글에 댓글을 작성하는 POST 메서드
     * </p>
     * @param : request (WriteCommentRequest) : 댓글 작성 요청에 필요한 데이터가 담긴 DTO 객체
     *                                  - content (String): 댓글 내용
     *          boardId (Long) : board 식별자
     * @return : ResponseEntity<ApiResponse<WriteCommentResponse>> : 댓글 내용과 http 응답을 담은 객체
     */
    @PostMapping
    public ResponseEntity<?> writeComment(@AuthenticationPrincipal(expression = "member") Member member,
                                          @RequestBody WriteCommentRequest request,
                                          @PathVariable Long boardId) {

        if (request.getContent().isBlank()) {
            return ApiResponse.badRequest("COMMENT-F001", "Content is blank");
        }

        WriteCommentResponse writeCommentResponse = commentService.writeComment(member, boardId, request);

        return ApiResponse.created("", "COMMENT-S001", writeCommentResponse, "Create Success");
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@AuthenticationPrincipal(expression = "member") Member member,
                                           @RequestBody UpdateCommentRequest request,
                                           @PathVariable Long boardId,
                                           @PathVariable Long commentId) {

        if (request.getContent().isBlank()) {
            return ApiResponse.badRequest("COMMENT-F001", "Content is blank");
        }

        UpdateCommentResponse response = commentService.updateComment(boardId, commentId, member, request);

        return ApiResponse.ok("COMMENT-S002", response, "Update Success");
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal(expression = "member") Member member,
                                           @PathVariable Long boardId,
                                           @PathVariable Long commentId) {
        commentService.deleteComment(boardId, commentId, member);

        return ApiResponse.ok("COMMENT-S003", new DeleteCommentResponse(commentId), "Delete Success");
    }
}
