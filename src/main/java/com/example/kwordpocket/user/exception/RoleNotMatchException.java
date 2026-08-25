package com.example.kwordpocket.user.exception;

import com.example.kwordpocket.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class RoleNotMatchException extends CustomException {
    public RoleNotMatchException() {
        super(HttpStatus.BAD_REQUEST, "유효하지 않은 Role입니다.");
    }
}
