package com.example.kwordpocket.deck.controller;

import com.example.kwordpocket.auth.dto.AuthUser;
import com.example.kwordpocket.deck.dto.DeckRequest;
import com.example.kwordpocket.deck.dto.DeckResponse;
import com.example.kwordpocket.deck.service.DeckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    // #1. 단어장 생성
    @PostMapping
    public ResponseEntity<DeckResponse> createDeck(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody DeckRequest request
    ) {
        DeckResponse response = deckService.createDeck(request, authUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // #2. 전체 단어장 목록 조회
    @GetMapping
    public ResponseEntity<List<DeckResponse>> getAllDecks() {
        List<DeckResponse> responses = deckService.getAllDecks();
        return ResponseEntity.ok(responses);
    }

    // #3. 단어장 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<DeckResponse> getDeck(@PathVariable("id") Long id) {
        DeckResponse response = deckService.getDeck(id);
        return ResponseEntity.ok(response);
    }

    // #4. 단어장 수정
    @PutMapping("/{id}")
    public ResponseEntity<DeckResponse> updateDeck(
            @PathVariable("id") Long id,
            @Valid @RequestBody DeckRequest request
    ) {
        DeckResponse response = deckService.updateDeck(id, request);
        return ResponseEntity.ok(response);
    }

    // #5. 단어장 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(@PathVariable("id") Long id) {
        deckService.deleteDeck(id);
        return ResponseEntity.noContent().build();
    }
}