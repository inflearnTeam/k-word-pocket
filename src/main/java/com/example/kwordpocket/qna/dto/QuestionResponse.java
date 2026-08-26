package com.example.kwordpocket.qna.dto;

import com.example.kwordpocket.qna.entity.Question;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class QuestionResponse {

    private final Long id;
    private final Long userId;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public QuestionResponse(Long id, Long userId, String title, String content,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static QuestionResponse from(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getUser().getId(),
                question.getTitle(),
                question.getContent(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }
}
