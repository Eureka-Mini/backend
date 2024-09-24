package com.dangun.miniproject.common.exception;

import com.dangun.miniproject.auth.exception.DuplicateException;
import com.dangun.miniproject.auth.exception.InvalidInputException;
import com.dangun.miniproject.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String AUTH = "AUTH-";
    private static final String BOARD = "BOARD-";
    private static final String COMMENT = "COMMENT-";
    private static final String MEMBER = "MEMBER-";

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<?> handleInvalidInputException(InvalidInputException e) {
        return ApiResponse.badRequest(AUTH + "F001", e.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<?> handleDuplicateException(DuplicateException e) {
        return ApiResponse.badRequest(AUTH + "F002", e.getMessage());
    }
}
