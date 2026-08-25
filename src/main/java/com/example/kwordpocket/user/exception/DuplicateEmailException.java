package com.example.kwordpocket.user.exception;

import com.example.kwordpocket.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends CustomException {

    public DuplicateEmailException() {
        super(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");
    }

    public DuplicateEmailException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}