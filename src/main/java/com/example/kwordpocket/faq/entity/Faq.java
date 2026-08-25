package com.example.kwordpocket.faq.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "faqs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Faq {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private String answer;

    public Faq(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public void update(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

}
