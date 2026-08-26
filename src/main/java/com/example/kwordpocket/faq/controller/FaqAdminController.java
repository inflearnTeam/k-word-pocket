package com.example.kwordpocket.faq.controller;

import com.example.kwordpocket.faq.dto.*;
import com.example.kwordpocket.faq.service.FaqService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/faqs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class FaqAdminController {
    private final FaqService faqService;

    @PostMapping
    public ResponseEntity<FaqResponse> createFaq(
            @Valid @RequestBody FaqCreateRequest request
    ) {
        FaqResponse response = faqService.createFaq(request);
        return ResponseEntity
                .created(URI.create("/faqs/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{faqId}")
    public ResponseEntity<FaqResponse> updateFaq(
            @PathVariable Long faqId,
            @Valid @RequestBody FaqUpdateRequest request
    ){
        return ResponseEntity.ok(faqService.updateFaq(faqId,request));
    }

    @DeleteMapping("/{faqId}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long faqId) {
        faqService.deleteFaq(faqId);
        return ResponseEntity.noContent().build();
    }
}
