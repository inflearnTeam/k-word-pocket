package com.example.kwordpocket.qna.controller;

import com.example.kwordpocket.auth.dto.AuthUser;
import com.example.kwordpocket.qna.dto.AnswerCreateRequest;
import com.example.kwordpocket.qna.dto.AnswerResponse;
import com.example.kwordpocket.qna.dto.AnswerUpdateRequest;
import com.example.kwordpocket.qna.dto.QuestionCreateRequest;
import com.example.kwordpocket.qna.dto.QuestionDetailResponse;
import com.example.kwordpocket.qna.dto.QuestionResponse;
import com.example.kwordpocket.qna.dto.QuestionUpdateRequest;
import com.example.kwordpocket.qna.exception.InvalidSortPropertyException;
import com.example.kwordpocket.qna.service.QnaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QnaController {

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("id", "title", "createdAt", "updatedAt");

    private final QnaService qnaService;

    @GetMapping
    public ResponseEntity<PagedModel<QuestionResponse>> getQuestions(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        validateSortProperties(pageable);
        return ResponseEntity.ok(new PagedModel<>(qnaService.getQuestions(pageable)));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDetailResponse> getQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(qnaService.getQuestion(questionId));
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody QuestionCreateRequest request
    ) {
        QuestionResponse response = qnaService.createQuestion(request, authUser);
        return ResponseEntity
                .created(URI.create("/questions/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionUpdateRequest request
    ) {
        return ResponseEntity.ok(qnaService.updateQuestion(questionId, request, authUser));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long questionId
    ) {
        qnaService.deleteQuestion(questionId, authUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{questionId}/answers")
    public ResponseEntity<List<AnswerResponse>> getAnswers(@PathVariable Long questionId) {
        return ResponseEntity.ok(qnaService.getAnswers(questionId));
    }

    @GetMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<AnswerResponse> getAnswer(
            @PathVariable Long questionId,
            @PathVariable Long answerId
    ) {
        return ResponseEntity.ok(qnaService.getAnswer(questionId, answerId));
    }

    @PostMapping("/{questionId}/answers")
    public ResponseEntity<AnswerResponse> createAnswer(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerCreateRequest request
    ) {
        AnswerResponse response = qnaService.createAnswer(questionId, request, authUser);
        return ResponseEntity
                .created(URI.create("/questions/" + questionId + "/answers/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<AnswerResponse> updateAnswer(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long questionId,
            @PathVariable Long answerId,
            @Valid @RequestBody AnswerUpdateRequest request
    ) {
        return ResponseEntity.ok(qnaService.updateAnswer(questionId, answerId, request, authUser));
    }

    @DeleteMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long questionId,
            @PathVariable Long answerId
    ) {
        qnaService.deleteAnswer(questionId, answerId, authUser);
        return ResponseEntity.noContent().build();
    }

    private void validateSortProperties(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!ALLOWED_SORT_PROPERTIES.contains(order.getProperty())) {
                throw new InvalidSortPropertyException();
            }
        }
    }
}
