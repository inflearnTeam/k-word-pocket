package com.example.kwordpocket.faq.dto;

import lombok.Getter;

@Getter
public class FaqCreateResponse {
    private final Long id;
    private final String question;
    private final String answer;

    public FaqCreateResponse(Long id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }
}
