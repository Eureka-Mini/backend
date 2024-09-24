package com.dangun.miniproject.board.exception;

import com.dangun.miniproject.common.exception.NotFoundException;

public class BoardNotFoundException extends NotFoundException {
    public BoardNotFoundException() {
        super("Board-", "Board not found");
    }
}