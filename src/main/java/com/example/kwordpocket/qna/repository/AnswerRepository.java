package com.example.kwordpocket.qna.repository;

import com.example.kwordpocket.qna.entity.Answer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findByIdAndQuestionId(Long id, Long questionId);

    @Modifying
    @Query("delete from Answer a where a.question.id = :questionId")
    void deleteAllByQuestionId(@Param("questionId") Long questionId);
}
