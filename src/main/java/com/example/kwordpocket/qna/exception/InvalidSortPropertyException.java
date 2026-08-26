package com.example.kwordpocket.qna.exception;

import com.example.kwordpocket.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidSortPropertyException extends CustomException {
    public InvalidSortPropertyException() {
        super(HttpStatus.BAD_REQUEST, "지원하지 않는 정렬 기준입니다.");
    }
}
