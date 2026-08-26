package com.example.kwordpocket.qna.dto;

import com.example.kwordpocket.qna.entity.Question;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class QuestionDetailResponse {

    private final Long id;
    private final Long userId;
    private final String title;
    private final String content;
    private final List<AnswerResponse> answers;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public QuestionDetailResponse(Long id, Long userId, String title, String content,
                                  List<AnswerResponse> answers,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.answers = answers;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static QuestionDetailResponse from(Question question) {
        return new QuestionDetailResponse(
                question.getId(),
                question.getUser().getId(),
                question.getTitle(),
                question.getContent(),
                question.getAnswers().stream()
                        .map(AnswerResponse::from)
                        .toList(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }
}
