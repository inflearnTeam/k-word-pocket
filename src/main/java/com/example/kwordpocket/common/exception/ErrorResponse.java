package com.example.kwordpocket.common.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
