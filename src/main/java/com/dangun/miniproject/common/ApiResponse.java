package com.dangun.miniproject.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;
    private final LocalDateTime timestamp;

    private ApiResponse(String code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String redirectUrl, String code, T data, String message) {
        return ResponseEntity.created(URI.create(redirectUrl)).body(new ApiResponse<>(code, data, message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String code, T data, String message) {
        return ResponseEntity.ok(new ApiResponse<>(code, data, message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String code, String message) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(code, null, message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String code, String message) {
        return ResponseEntity.status(403).body(new ApiResponse<>(code, null, message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> unAuthorized(String code, String message) {
        return ResponseEntity.status(401).body(new ApiResponse<>(code, null, message));
    }
}
