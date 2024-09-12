package com.dangun.miniproject.dto;

import com.dangun.miniproject.domain.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBoardRequest {
    private String title;
    private String content;
    private Integer price;
    private BoardStatus boardStatus;
}
