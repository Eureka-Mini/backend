package com.dangun.miniproject.common.exception;

import com.dangun.miniproject.auth.exception.DuplicateException;
import com.dangun.miniproject.auth.exception.InvalidInputException;
import com.dangun.miniproject.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String AUTH = "AUTH-";

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<?> handleInvalidInputException(InvalidInputException e) {
        return ApiResponse.badRequest(AUTH + "F001", e.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<?> handleDuplicateException(DuplicateException e) {
        return ApiResponse.badRequest(AUTH + "F002", e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        return ApiResponse.forbidden(AUTH + "F301", e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return ApiResponse.unAuthorized(AUTH + "F101", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNoSuchElementException(NotFoundException e) {
        return ApiResponse.badRequest(e.getPrefix() + "F101", e.getMessage());
    }
}