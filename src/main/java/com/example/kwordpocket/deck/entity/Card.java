package com.example.kwordpocket.deck.entity;

import com.example.kwordpocket.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "card")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String front; // 단어

    @Column(nullable = false)
    private String back;  // 뜻

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Builder
    public Card(String front, String back, Deck deck) {
        this.front = front;
        this.back = back;
        this.deck = deck;
    }

    public void update(String front, String back) {
        this.front = front;
        this.back = back;
    }

}
