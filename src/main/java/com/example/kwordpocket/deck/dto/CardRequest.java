package com.example.kwordpocket.deck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CardRequest {

    @NotNull(message = "단어장 ID는 필수입니다.")
    private Long deckId;

    @NotBlank(message = "단어(앞면)를 입력해주세요.")
    private String front;

    @NotBlank(message = "뜻(뒷면)을 입력해주세요.")
    private String back;

    public CardRequest(Long deckId, String front, String back) {
        this.deckId = deckId;
        this.front = front;
        this.back = back;
    }
}
