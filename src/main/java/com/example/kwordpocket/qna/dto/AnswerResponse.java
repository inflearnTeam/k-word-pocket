package com.example.kwordpocket.qna.dto;

import com.example.kwordpocket.qna.entity.Answer;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AnswerResponse {

    private final Long id;
    private final Long questionId;
    private final Long userId;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public AnswerResponse(Long id, Long questionId, Long userId, String content,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.questionId = questionId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AnswerResponse from(Answer answer) {
        return new AnswerResponse(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getUser().getId(),
                answer.getContent(),
                answer.getCreatedAt(),
                answer.getUpdatedAt()
        );
    }
}
