package com.example.kwordpocket.faq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class FaqUpdateRequest {
    @NotBlank(message = "질문을 입력해주세요.")
    @Size(max = 200, message = "질문은 200자 이하로 입력해주세요.")
    private String question;
    @NotBlank(message = "대답을 입력해주세요.")
    @Size(max = 16000, message = "대답은 16000자 이하로 입력해주세요.")
    private String answer;
}
