package com.example.kwordpocket.faq.controller;

import com.example.kwordpocket.faq.dto.FaqResponse;
import com.example.kwordpocket.faq.exception.InvalidSortPropertyException;
import com.example.kwordpocket.faq.service.FaqService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/faqs")
@RequiredArgsConstructor
public class FaqController {

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("id", "question", "createdAt", "updatedAt");

    private final FaqService faqService;

    @GetMapping
    public ResponseEntity<PagedModel<FaqResponse>> getAllFaq(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        validateSortProperties(pageable);
        return ResponseEntity.ok(new PagedModel<>(faqService.getAllFaq(pageable)));
    }

    @GetMapping("/{faqId}")
    public ResponseEntity<FaqResponse> getOneFaq(
            @PathVariable Long faqId
    ) {
        return ResponseEntity.ok(faqService.getOneFaq(faqId));
    }

    private void validateSortProperties(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!ALLOWED_SORT_PROPERTIES.contains(order.getProperty())) {
                throw new InvalidSortPropertyException();
            }
        }
    }
}
