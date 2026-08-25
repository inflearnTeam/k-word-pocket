package com.example.kwordpocket.user.exception;

import com.example.kwordpocket.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class PasswordNotMatchException extends CustomException {
    public PasswordNotMatchException() {
        super(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
    }
}
