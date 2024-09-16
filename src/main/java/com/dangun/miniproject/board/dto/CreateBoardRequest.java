package com.dangun.miniproject.board.dto;

import com.dangun.miniproject.board.domain.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBoardRequest {
    private String title;
    private String content;
    private Integer price;
    private BoardStatus boardStatus = BoardStatus.판매중;  //status 기본 값을 '판매중'으로 설정
    private Long memberId;
}