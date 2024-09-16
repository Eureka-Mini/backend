package com.dangun.miniproject.auth.exception;

import com.dangun.miniproject.auth.exception.exceptions.DuplicateEmailException;
import com.dangun.miniproject.auth.exception.exceptions.DuplicateNicknameException;
import com.dangun.miniproject.auth.exception.exceptions.InvalidEmailException;
import com.dangun.miniproject.auth.exception.exceptions.ReissueAccessTokenException;
import com.dangun.miniproject.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalSignupException {

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<?> handleInvalidEmailException(InvalidEmailException e) {
        return ApiResponse.badRequest("MEMBER-F000", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.badRequest("MEMBER-F000", e.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<?> handleDuplicateEmailException(DuplicateEmailException e) {
        return ApiResponse.badRequest("MEMBER-F009", e.getMessage());
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<?> handleDuplicateNicknameException(DuplicateNicknameException e) {
        return ApiResponse.badRequest("MEMBER-F009", e.getMessage());
    }

    @ExceptionHandler(ReissueAccessTokenException.class)
    public ResponseEntity<?> handleReissueAccessTokenException(ReissueAccessTokenException e) {
        return ApiResponse.badRequest("AUTH_F000", e.getMessage());
    }
}
