package com.example.kwordpocket.faq.service;

import com.example.kwordpocket.faq.dto.*;
import com.example.kwordpocket.faq.entity.Faq;
import com.example.kwordpocket.faq.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    @Transactional
    public FaqCreateResponse createFaq(FaqCreateRequest request) {
        Faq faq = new Faq(request.getQuestion(), request.getAnswer());
        faqRepository.save(faq);
        return new FaqCreateResponse(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer()
        );
    }

    @Transactional(readOnly = true)
    public List<FaqGetResponse> getAllFaq() {
        List<Faq> faqs = faqRepository.findAll();
        return faqs.stream().map(
                faq -> new FaqGetResponse(
                        faq.getId(),
                        faq.getQuestion(),
                        faq.getAnswer()
                )).toList();
    }

    @Transactional(readOnly = true)
    public FaqGetResponse getOneFaq(Long faqId) {
        Faq faq = faqRepository.findById(faqId).orElseThrow(
                () -> new FaqNotFoundException("존재하지 않는 Faq 입니다.")
        );
        return new FaqGetResponse(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer()
        );
    }

    @Transactional
    public FaqUpdateResponse updateFaq(Long faqId, FaqUpdateRequest request) {
        Faq faq = faqRepository.findById(faqId).orElseThrow(
                () -> new FaqNotFoundException("존재하지 않는 Faq 입니다.")
        );
        faq.update(request.getQuestion(), request.getAnswer());
        return new FaqUpdateResponse(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer()
        );
    }

    @Transactional
    public void deleteFaq(Long faqId) {
        boolean existence = faqRepository.existsById(faqId);
        if (!existence) {
            throw new FaqNotFoundException("존재하지 않는 Faq 입니다.");
        }
        faqRepository.deleteById(faqId);
    }
}
