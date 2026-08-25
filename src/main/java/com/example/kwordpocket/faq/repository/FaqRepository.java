package com.example.kwordpocket.faq.repository;

import com.example.kwordpocket.faq.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository <Faq, Long> {
}
