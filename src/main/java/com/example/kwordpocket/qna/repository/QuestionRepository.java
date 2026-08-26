package com.example.kwordpocket.qna.repository;

import com.example.kwordpocket.qna.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
