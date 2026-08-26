package com.example.kwordpocket.qna.exception;

import com.example.kwordpocket.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class QuestionNotFoundException extends CustomException {
    public QuestionNotFoundException() {
        super(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다.");
    }
}
