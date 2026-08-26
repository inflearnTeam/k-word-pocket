package com.example.kwordpocket.qna.exception;

import com.example.kwordpocket.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class QnaAccessDeniedException extends CustomException {
    public QnaAccessDeniedException() {
        super(HttpStatus.FORBIDDEN, "해당 작업에 대한 권한이 없습니다.");
    }
}
