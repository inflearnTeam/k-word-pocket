package com.example.kwordpocket.deck.dto;

import com.example.kwordpocket.deck.entity.Card;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CardResponse {

    private final Long id;
    private final Long deckId;
    private final String front;
    private final String back;
    private final LocalDateTime createdAt;

    public CardResponse(Card card) {
        this.id = card.getId();
        this.deckId = card.getDeck().getId();
        this.front = card.getFront();
        this.back = card.getBack();
        this.createdAt = card.getCreatedAt();
    }
}