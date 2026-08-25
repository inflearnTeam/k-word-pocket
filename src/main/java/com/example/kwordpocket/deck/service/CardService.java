package com.example.kwordpocket.deck.service;

import com.example.kwordpocket.deck.dto.CardRequest;
import com.example.kwordpocket.deck.dto.CardResponse;
import com.example.kwordpocket.deck.entity.Card;
import com.example.kwordpocket.deck.entity.Deck;
import com.example.kwordpocket.deck.repository.CardRepository;
import com.example.kwordpocket.deck.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    // #1. 카드 생성
    @Transactional
    public CardResponse createCard(CardRequest request, Long loginUserId) {
        // 1. 단어장(Deck) 조회 -> 단어장을 먼저 가져와야함.
        Deck deck = deckRepository.findById(request.getDeckId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단어장입니다. id=" + request.getDeckId()));

        // 2. 단어장 소유자 검증
        if (deck.getUser() != null && !deck.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("해당 단어장에 카드를 추가할 권한이 없습니다.");
        }

        // 3. Entity 변환 및 DB 저장
        Card card = Card.builder()
                .front(request.getFront())
                .back(request.getBack())
                .deck(deck)
                .build();

        return new CardResponse(cardRepository.save(card));
    }

    // #2. 특정 단어장의 카드 목록 조회 (조회는 모든 유저 허용 또는 권한 체크 선택)
    public List<CardResponse> getCardsByDeckId(Long deckId) {
        if (!deckRepository.existsById(deckId)) {
            throw new IllegalArgumentException("존재하지 않는 단어장입니다. id=" + deckId);
        }
        return cardRepository.findByDeckId(deckId).stream()
                .map(CardResponse::new)
                .collect(Collectors.toList());
    }

    // #3. 카드 단건 상세 조회
    public CardResponse getCardById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드입니다. id=" + cardId));
        return new CardResponse(card);
    }

    // #4. 카드 수정
    @Transactional
    public CardResponse updateCard(Long cardId, CardRequest request, Long loginUserId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드입니다. id=" + cardId));

        validateDeckOwner(card.getDeck(), loginUserId);

        card.update(request.getFront(), request.getBack());
        return new CardResponse(card);
    }

    // #5. 카드 삭제
    @Transactional
    public void deleteCard(Long cardId, Long loginUserId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카드입니다. id=" + cardId));

        validateDeckOwner(card.getDeck(), loginUserId);

        cardRepository.delete(card);
    }

    // #0. 소유자 검증 공통 메서드
    private void validateDeckOwner(Deck deck, Long loginUserId) {
        if (deck.getUser() != null && !deck.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("해당 단어장에 대한 작업 권한이 없습니다.");
        }
    }
}