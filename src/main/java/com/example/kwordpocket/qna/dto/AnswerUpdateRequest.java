package com.example.kwordpocket.qna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnswerUpdateRequest {

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 16000, message = "내용은 16000자 이하로 입력해주세요.")
    private String content;
}
