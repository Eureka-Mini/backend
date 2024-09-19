package com.dangun.miniproject.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WriteBoardRequest {
    private final String title;
    private final String content;
    private final int price;
}
