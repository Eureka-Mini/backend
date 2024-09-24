package com.dangun.miniproject.common.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String prefix;

    public NotFoundException(String prefix, String message) {
        super(message);
        this.prefix = prefix;
    }
}