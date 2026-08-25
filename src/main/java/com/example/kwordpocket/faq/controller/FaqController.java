package com.example.kwordpocket.faq.controller;

import com.example.kwordpocket.faq.dto.*;
import com.example.kwordpocket.faq.service.FaqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FaqController {
    private final FaqService faqService;

    @PostMapping("/admin/faqs")
    public ResponseEntity<FaqCreateResponse> createFaq(
            @Valid @RequestBody FaqCreateRequest request
    ) {
        return ResponseEntity.ok(faqService.createFaq(request));
    }

    @GetMapping("/admin/faqs")
    public ResponseEntity<List<FaqGetResponse>> getAllFaqForAdmin(
    ) {
        return ResponseEntity.ok(faqService.getAllFaq());
    }

    @GetMapping("/admin/faqs/{faqId}")
    public ResponseEntity<FaqGetResponse> getOneFaqForAdmin(
            @PathVariable Long faqId
    ) {
        return ResponseEntity.ok(faqService.getOneFaq(faqId));
    }

    @PutMapping("/admin/faqs/{faqId}")
    public ResponseEntity<FaqUpdateResponse> updateFaq(
            @PathVariable Long faqId,
            @Valid @RequestBody FaqUpdateRequest request
    ){
        return ResponseEntity.ok(faqService.updateFaq(faqId,request));
    }

    @DeleteMapping("/admin/faqs/{faqId}")
    public void deleteFaq(@PathVariable Long faqId) {
        faqService.deleteFaq(faqId);
    }

    @GetMapping("/faqs")
    public ResponseEntity<List<FaqGetResponse>> getAllFaq() {
        return ResponseEntity.ok(faqService.getAllFaq());
    }


    @GetMapping("/faqs/{faqId}")
    public ResponseEntity<FaqGetResponse> getOneFaq(
            @PathVariable Long faqId
    ) {
        return ResponseEntity.ok(faqService.getOneFaq(faqId));
    }
}
