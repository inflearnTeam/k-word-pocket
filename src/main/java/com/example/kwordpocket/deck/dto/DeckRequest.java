package com.example.kwordpocket.deck.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeckRequest {

    @NotBlank(message = "단어장 이름을 입력해주세요.")
    private String name;

    public DeckRequest(String name) {
        this.name = name;
    }
}