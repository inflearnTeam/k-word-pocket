package com.example.kwordpocket.deck.dto;

import com.example.kwordpocket.deck.entity.Deck;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DeckResponse {

    private final Long id;
    private final String name;
    private final LocalDateTime createdAt;

    public DeckResponse(Deck deck) {
        this.id = deck.getId();
        this.name = deck.getName();
        this.createdAt = deck.getCreatedAt();
    }
}