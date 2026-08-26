package com.example.kwordpocket.faq.dto;

import com.example.kwordpocket.faq.entity.Faq;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class FaqResponse {

    private final Long id;
    private final String question;
    private final String answer;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public FaqResponse(Long id, String question, String answer,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static FaqResponse from(Faq faq) {
        return new FaqResponse(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getCreatedAt(),
                faq.getUpdatedAt()
        );
    }
}
