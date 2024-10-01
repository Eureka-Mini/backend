package com.dangun.miniproject.board.dto;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.board.domain.BoardStatus;
import com.dangun.miniproject.comment.dto.GetCommentResponse;
import com.dangun.miniproject.common.code.CodeKey;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class GetBoardDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private String writerStreetAddress;
    private Integer price;
//    private BoardStatus boardStatus;
    private CodeKey codeKey;
    private LocalDateTime createdAt;
    private List<GetCommentResponse> comments = new ArrayList<>();

    public static GetBoardDetailResponse from(final Board board) {
        final GetBoardDetailResponse boardResponse = new GetBoardDetailResponse();

        boardResponse.id = board.getId();
        boardResponse.title = board.getTitle();
        boardResponse.content = board.getContent();
        boardResponse.writer = board.getMember().getNickname();
        boardResponse.writerStreetAddress = board.getMember().getAddress().getStreet();
        boardResponse.price = board.getPrice();
        boardResponse.codeKey=board.getCodeKey();
//        boardResponse.boardStatus = board.getBoardStatus();
        boardResponse.createdAt = board.getCreatedAt();

        return boardResponse;
    }
}
