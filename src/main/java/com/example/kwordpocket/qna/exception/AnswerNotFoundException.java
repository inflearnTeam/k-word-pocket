package com.example.kwordpocket.qna.exception;

import com.example.kwordpocket.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AnswerNotFoundException extends CustomException {
    public AnswerNotFoundException() {
        super(HttpStatus.NOT_FOUND, "답변을 찾을 수 없습니다.");
    }
}
