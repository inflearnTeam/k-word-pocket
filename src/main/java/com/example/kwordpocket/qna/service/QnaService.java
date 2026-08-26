package com.example.kwordpocket.qna.service;

import com.example.kwordpocket.auth.dto.AuthUser;
import com.example.kwordpocket.qna.dto.AnswerCreateRequest;
import com.example.kwordpocket.qna.dto.AnswerResponse;
import com.example.kwordpocket.qna.dto.AnswerUpdateRequest;
import com.example.kwordpocket.qna.dto.QuestionCreateRequest;
import com.example.kwordpocket.qna.dto.QuestionDetailResponse;
import com.example.kwordpocket.qna.dto.QuestionResponse;
import com.example.kwordpocket.qna.dto.QuestionUpdateRequest;
import com.example.kwordpocket.qna.entity.Answer;
import com.example.kwordpocket.qna.entity.Question;
import com.example.kwordpocket.qna.exception.AnswerNotFoundException;
import com.example.kwordpocket.qna.exception.QnaAccessDeniedException;
import com.example.kwordpocket.qna.exception.QuestionNotFoundException;
import com.example.kwordpocket.qna.repository.AnswerRepository;
import com.example.kwordpocket.qna.repository.QuestionRepository;
import com.example.kwordpocket.user.entity.User;
import com.example.kwordpocket.user.enums.Role;
import com.example.kwordpocket.user.exception.UserNotFoundException;
import com.example.kwordpocket.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public Page<QuestionResponse> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(QuestionResponse::from);
    }

    public QuestionDetailResponse getQuestion(Long questionId) {
        return QuestionDetailResponse.from(findQuestionById(questionId));
    }

    @Transactional
    public QuestionResponse createQuestion(QuestionCreateRequest request, AuthUser authUser) {
        User user = findUserById(authUser.getId());
        Question question = Question.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        return QuestionResponse.from(questionRepository.save(question));
    }

    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionUpdateRequest request, AuthUser authUser) {
        Question question = findQuestionById(questionId);
        validateAuthor(question.getUser().getId(), authUser);
        question.update(request.getTitle(), request.getContent());
        questionRepository.flush();
        return QuestionResponse.from(question);
    }

    @Transactional
    public void deleteQuestion(Long questionId, AuthUser authUser) {
        Question question = findQuestionById(questionId);
        validateAuthorOrAdmin(question.getUser().getId(), authUser);
        answerRepository.deleteAllByQuestionId(questionId);
        questionRepository.delete(question);
    }

    public List<AnswerResponse> getAnswers(Long questionId) {
        return findQuestionById(questionId).getAnswers().stream()
                .map(AnswerResponse::from)
                .toList();
    }

    public AnswerResponse getAnswer(Long questionId, Long answerId) {
        return AnswerResponse.from(findAnswerInQuestion(questionId, answerId));
    }

    @Transactional
    public AnswerResponse createAnswer(Long questionId, AnswerCreateRequest request, AuthUser authUser) {
        Question question = findQuestionById(questionId);
        User user = findUserById(authUser.getId());
        Answer answer = Answer.builder()
                .question(question)
                .user(user)
                .content(request.getContent())
                .build();
        return AnswerResponse.from(answerRepository.save(answer));
    }

    @Transactional
    public AnswerResponse updateAnswer(Long questionId, Long answerId, AnswerUpdateRequest request, AuthUser authUser) {
        Answer answer = findAnswerInQuestion(questionId, answerId);
        validateAuthor(answer.getUser().getId(), authUser);
        answer.update(request.getContent());
        answerRepository.flush();
        return AnswerResponse.from(answer);
    }

    @Transactional
    public void deleteAnswer(Long questionId, Long answerId, AuthUser authUser) {
        Answer answer = findAnswerInQuestion(questionId, answerId);
        validateAuthorOrAdmin(answer.getUser().getId(), authUser);
        answerRepository.delete(answer);
    }

    private void validateAuthor(Long authorId, AuthUser authUser) {
        if (!authorId.equals(authUser.getId())) {
            throw new QnaAccessDeniedException();
        }
    }

    private void validateAuthorOrAdmin(Long authorId, AuthUser authUser) {
        if (!authorId.equals(authUser.getId()) && authUser.getRole() != Role.ROLE_ADMIN) {
            throw new QnaAccessDeniedException();
        }
    }

    private Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException());
    }

    private Answer findAnswerInQuestion(Long questionId, Long answerId) {
        return answerRepository.findByIdAndQuestionId(answerId, questionId)
                .orElseThrow(() -> new AnswerNotFoundException());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());
    }
}
