package com.dangun.miniproject.service;

import com.dangun.miniproject.dto.GetBoardDetailResponse;

public interface BoardService {

	GetBoardDetailResponse getBoardDetail(final Long boardId);
}
