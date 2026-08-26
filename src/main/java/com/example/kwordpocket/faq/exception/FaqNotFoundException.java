package com.example.kwordpocket.faq.exception;

import com.example.kwordpocket.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class FaqNotFoundException extends CustomException {
    public FaqNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 Faq 입니다.");
    }

}
