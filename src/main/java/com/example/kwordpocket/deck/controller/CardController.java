package com.example.kwordpocket.deck.controller;

import com.example.kwordpocket.auth.dto.AuthUser;
import com.example.kwordpocket.deck.dto.CardRequest;
import com.example.kwordpocket.deck.dto.CardResponse;
import com.example.kwordpocket.deck.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // #1. 카드 생성
    @PostMapping
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody CardRequest request,
            AuthUser authUser
    ) {
        CardResponse response = cardService.createCard(request, authUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // #2. 특정 단어장의 카드 목록 조회
    @GetMapping
    public ResponseEntity<List<CardResponse>> getCardsByDeck(@RequestParam Long deckId) {
        return ResponseEntity.ok(cardService.getCardsByDeckId(deckId));
    }

    // #3. 카드 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    // #4. 카드 수정
    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody CardRequest request,
            AuthUser authUser
    ) {
        return ResponseEntity.ok(cardService.updateCard(id, request, authUser.getId()));
    }

    // #5. 카드 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long id,
            AuthUser authUser
    ) {
        cardService.deleteCard(id, authUser.getId());
        return ResponseEntity.noContent().build();
    }
}