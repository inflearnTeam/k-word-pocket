package com.example.kwordpocket.faq.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FaqUpdateRequest {
    @NotBlank(message = "질문을 입력해주세요.")
    private String question;
    @NotBlank(message = "대답을 입력해주세요.")
    private String answer;
}
