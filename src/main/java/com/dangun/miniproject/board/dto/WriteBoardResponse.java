package com.dangun.miniproject.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class WriteBoardResponse {
    private Long id;

    public WriteBoardResponse(Long id) {
        this.id = id;
    }
}