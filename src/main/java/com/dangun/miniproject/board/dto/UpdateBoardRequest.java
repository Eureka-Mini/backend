package com.dangun.miniproject.board.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBoardRequest {
    private String title;
    private String content;
    private Long price;
    private String boardStatus;
}