package com.example.kwordpocket.notice.dto;

import com.example.kwordpocket.notice.entity.Notice;
import java.time.LocalDateTime;

public record CreateNoticeResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CreateNoticeResponse from(Notice notice) {
        return new CreateNoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }
}
