
package com.dangun.miniproject.board.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class DeleteBoardResponse {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;
}