package com.example.kwordpocket.user.exception;

import com.example.kwordpocket.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class EmailNotFoundException extends CustomException {
    public EmailNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "존재하지 않는 이메일입니다.");
    }
}