package com.dangun.miniproject.board.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class WriteBoardResponse {
    private final String code;
    private final String message;
    private final BoardData data;
    private final LocalDateTime timestamp;

    @Getter
    @Builder
    public static class BoardData {
        private final Long id;
    }
}