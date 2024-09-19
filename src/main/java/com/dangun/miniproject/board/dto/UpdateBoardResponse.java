package com.dangun.miniproject.board.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateBoardResponse {
    private final String code;
    private final String message;
    private final Data data;
    private final LocalDateTime timestamp;

    @Getter
    @Builder
    public static class Data {
        private final String content;

    }
}