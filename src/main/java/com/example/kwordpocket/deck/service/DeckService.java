package com.example.kwordpocket.deck.service;

import com.example.kwordpocket.auth.dto.AuthUser;
import com.example.kwordpocket.deck.dto.DeckRequest;
import com.example.kwordpocket.deck.dto.DeckResponse;
import com.example.kwordpocket.deck.entity.Deck;
import com.example.kwordpocket.deck.repository.DeckRepository;
import com.example.kwordpocket.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeckService {

    private final DeckRepository deckRepository;

    // #1. 단어장 생성
    @Transactional
    public DeckResponse createDeck(DeckRequest request, AuthUser authUser) {
        User user = (authUser != null) ? User.fromAuthUser(authUser) : null;

        Deck deck = Deck.builder()
                .name(request.getName())
                .user(user)
                .build();

        Deck savedDeck = deckRepository.save(deck);
        return new DeckResponse(savedDeck);
    }

    // #2. 단어장 목록 조회
    public List<DeckResponse> getAllDecks() {
        return deckRepository.findAll().stream()
                .map(DeckResponse::new)
                .collect(Collectors.toList());
    }

    // #3. 단어장 단건 조회
    public DeckResponse getDeck(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단어장입니다. id=" + deckId));
        return new DeckResponse(deck);
    }

    // #4. 단어장 수정
    @Transactional
    public DeckResponse updateDeck(Long deckId, DeckRequest request) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단어장입니다. id=" + deckId));

        deck.updateName(request.getName());
        return new DeckResponse(deck);
    }

    // #5. 단어장 삭제
    @Transactional
    public void deleteDeck(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단어장입니다. id=" + deckId));

        deckRepository.delete(deck);
    }
}