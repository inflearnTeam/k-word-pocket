package com.example.kwordpocket.faq.service;

import com.example.kwordpocket.faq.dto.*;
import com.example.kwordpocket.faq.entity.Faq;
import com.example.kwordpocket.faq.exception.FaqNotFoundException;
import com.example.kwordpocket.faq.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    @Transactional
    public FaqResponse createFaq(FaqCreateRequest request) {
        Faq faq = new Faq(request.getQuestion(), request.getAnswer());
        return FaqResponse.from(faqRepository.save(faq));
    }

    @Transactional(readOnly = true)
    public Page<FaqResponse> getAllFaq(Pageable pageable) {
        return faqRepository.findAll(pageable)
                .map(FaqResponse::from);
    }

    @Transactional(readOnly = true)
    public FaqResponse getOneFaq(Long faqId) {
        return FaqResponse.from(findFaqById(faqId));
    }

    @Transactional
    public FaqResponse updateFaq(Long faqId, FaqUpdateRequest request) {
        Faq faq = findFaqById(faqId);
        faq.update(request.getQuestion(), request.getAnswer());
        faqRepository.flush();
        return FaqResponse.from(faq);
    }

    @Transactional
    public void deleteFaq(Long faqId) {
        faqRepository.delete(findFaqById(faqId));
    }

    private Faq findFaqById(Long faqId) {
        return faqRepository.findById(faqId)
                .orElseThrow(FaqNotFoundException::new);
    }
}
