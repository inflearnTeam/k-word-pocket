package com.example.kwordpocket.qna;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.kwordpocket.common.config.JwtUtil;
import com.example.kwordpocket.qna.entity.Answer;
import com.example.kwordpocket.qna.entity.Question;
import com.example.kwordpocket.qna.repository.AnswerRepository;
import com.example.kwordpocket.qna.repository.QuestionRepository;
import com.example.kwordpocket.user.entity.User;
import com.example.kwordpocket.user.enums.Role;
import com.example.kwordpocket.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci1qdW5pdC10ZXN0aW5nLW9ubHktNDhieXRlcw==")
@AutoConfigureMockMvc
@Transactional
@DisplayName("QnA API")
class QnaApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired QuestionRepository questionRepository;
    @Autowired AnswerRepository answerRepository;
    @Autowired JwtUtil jwtUtil;
    @PersistenceContext EntityManager entityManager;

    private User author;
    private User other;
    private User admin;
    private String authorToken;
    private String otherToken;
    private String adminToken;
    private Question question;
    private Answer answer;

    @BeforeEach
    void setUp() {
        author = createUser("author@test.com", Role.ROLE_USER);
        other = createUser("other@test.com", Role.ROLE_USER);
        admin = createUser("admin@test.com", Role.ROLE_ADMIN);
        authorToken = tokenOf(author);
        otherToken = tokenOf(other);
        adminToken = tokenOf(admin);

        question = questionRepository.save(Question.builder()
                .user(author)
                .title("질문 제목")
                .content("질문 내용")
                .build());
        answer = answerRepository.save(Answer.builder()
                .question(question)
                .user(other)
                .content("답변 내용")
                .build());

        // 준비 데이터를 DB에 반영하고 영속성 컨텍스트를 비워, 요청이 실제 조회 경로를 타도록 한다
        entityManager.flush();
        entityManager.clear();
    }

    private User createUser(String email, Role role) {
        return userRepository.save(new User(email, "encoded-password", role));
    }

    private String tokenOf(User user) {
        return "Bearer " + jwtUtil.createToken(user.getId(), user.getEmail(), user.getRole());
    }

    private static String questionBody(String title, String content) {
        return """
                {"title":"%s","content":"%s"}""".formatted(title, content);
    }

    private static String answerBody(String content) {
        return """
                {"content":"%s"}""".formatted(content);
    }

    @Nested
    @DisplayName("수정 권한 - 작성자 본인만 허용")
    class UpdateAuthorization {

        @Test
        @DisplayName("타인은 질문을 수정할 수 없다")
        void 타인_질문수정_403() throws Exception {
            mockMvc.perform(put("/questions/{id}", question.getId())
                            .header(HttpHeaders.AUTHORIZATION, otherToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(questionBody("탈취된 제목", "탈취된 내용")))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("FORBIDDEN"));
        }

        @Test
        @DisplayName("관리자도 타인의 질문은 수정할 수 없다")
        void 관리자_질문수정_403() throws Exception {
            mockMvc.perform(put("/questions/{id}", question.getId())
                            .header(HttpHeaders.AUTHORIZATION, adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(questionBody("관리자 수정", "관리자 내용")))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("작성자 본인은 질문을 수정할 수 있다")
        void 본인_질문수정_200() throws Exception {
            mockMvc.perform(put("/questions/{id}", question.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(questionBody("수정된 제목", "수정된 내용")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("수정된 제목"));
        }

        @Test
        @DisplayName("타인은 답변을 수정할 수 없다")
        void 타인_답변수정_403() throws Exception {
            mockMvc.perform(put("/questions/{qid}/answers/{aid}", question.getId(), answer.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(answerBody("탈취된 답변")))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("답변 작성자 본인은 답변을 수정할 수 있다")
        void 본인_답변수정_200() throws Exception {
            mockMvc.perform(put("/questions/{qid}/answers/{aid}", question.getId(), answer.getId())
                            .header(HttpHeaders.AUTHORIZATION, otherToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(answerBody("수정된 답변")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").value("수정된 답변"));
        }
    }

    @Nested
    @DisplayName("삭제 권한 - 작성자 본인 또는 관리자")
    class DeleteAuthorization {

        @Test
        @DisplayName("타인은 질문을 삭제할 수 없다")
        void 타인_질문삭제_403() throws Exception {
            mockMvc.perform(delete("/questions/{id}", question.getId())
                            .header(HttpHeaders.AUTHORIZATION, otherToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("작성자 본인은 질문을 삭제할 수 있다")
        void 본인_질문삭제_204() throws Exception {
            mockMvc.perform(delete("/questions/{id}", question.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorToken))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("관리자는 타인의 질문을 삭제할 수 있다")
        void 관리자_질문삭제_204() throws Exception {
            mockMvc.perform(delete("/questions/{id}", question.getId())
                            .header(HttpHeaders.AUTHORIZATION, adminToken))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("타인은 답변을 삭제할 수 없다")
        void 타인_답변삭제_403() throws Exception {
            mockMvc.perform(delete("/questions/{qid}/answers/{aid}", question.getId(), answer.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("관리자는 타인의 답변을 삭제할 수 있다")
        void 관리자_답변삭제_204() throws Exception {
            mockMvc.perform(delete("/questions/{qid}/answers/{aid}", question.getId(), answer.getId())
                            .header(HttpHeaders.AUTHORIZATION, adminToken))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("질문을 삭제하면 달린 답변도 함께 사라진다")
        void 질문삭제시_답변도_삭제() throws Exception {
            answerRepository.save(Answer.builder()
                    .question(question).user(other).content("두 번째 답변").build());

            mockMvc.perform(delete("/questions/{id}", question.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorToken))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/questions/{id}", question.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorToken))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("작성자 결정 - 요청 body가 아닌 토큰 기준")
    class AuthorResolution {

        @Test
        @DisplayName("body에 userId를 넣어도 토큰의 사용자가 작성자가 된다")
        void 질문_작성자위조_차단() throws Exception {
            String forged = """
                    {"userId":%d,"title":"위조 시도","content":"내용"}""".formatted(other.getId());

            mockMvc.perform(post("/questions")
                            .header(HttpHeaders.AUTHORIZATION, authorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forged))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").value(author.getId()));
        }

        @Test
        @DisplayName("답변도 body의 userId가 아닌 토큰 사용자로 작성된다")
        void 답변_작성자위조_차단() throws Exception {
            String forged = """
                    {"userId":%d,"content":"위조 시도"}""".formatted(author.getId());

            mockMvc.perform(post("/questions/{id}/answers", question.getId())
                            .header(HttpHeaders.AUTHORIZATION, otherToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forged))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").value(other.getId()));
        }

        @Test
        @DisplayName("인증 없이는 질문을 작성할 수 없다")
        void 미인증_작성_401() throws Exception {
            mockMvc.perform(post("/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(questionBody("제목", "내용")))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("입력 검증")
    class InputValidation {

        @Test
        @DisplayName("제목이 비면 400")
        void 제목_공백_400() throws Exception {
            mockMvc.perform(post("/questions")
                            .header(HttpHeaders.AUTHORIZATION, authorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(questionBody("  ", "내용")))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("제목이 200자를 넘으면 400")
        void 제목_초과_400() throws Exception {
            mockMvc.perform(post("/questions")
                            .header(HttpHeaders.AUTHORIZATION, authorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(questionBody("가".repeat(201), "내용")))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("내용이 16000자를 넘으면 저장 전에 400")
        void 내용_초과_400() throws Exception {
            mockMvc.perform(post("/questions")
                            .header(HttpHeaders.AUTHORIZATION, authorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(questionBody("제목", "가".repeat(16001))))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("내용이 16000자면 저장된다")
        void 내용_경계_201() throws Exception {
            mockMvc.perform(post("/questions")
                            .header(HttpHeaders.AUTHORIZATION, authorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(questionBody("제목", "가".repeat(16000))))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("목록 조회와 정렬")
    class Listing {

        @Test
        @DisplayName("허용된 정렬 기준은 통과한다")
        void 허용_정렬_200() throws Exception {
            mockMvc.perform(get("/questions").param("sort", "id,desc")
                            .header(HttpHeaders.AUTHORIZATION, authorToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.page.size").exists());
        }

        @Test
        @DisplayName("존재하지 않는 정렬 기준은 500이 아니라 400")
        void 잘못된_정렬_400() throws Exception {
            mockMvc.perform(get("/questions").param("sort", "asdf")
                            .header(HttpHeaders.AUTHORIZATION, authorToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
        }

        @Test
        @DisplayName("연관 엔티티 경로로는 정렬할 수 없다")
        void 중첩경로_정렬_400() throws Exception {
            mockMvc.perform(get("/questions").param("sort", "user.password,desc")
                            .header(HttpHeaders.AUTHORIZATION, authorToken))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("상세 조회에 답변 목록이 포함된다")
        void 상세조회_답변포함() throws Exception {
            mockMvc.perform(get("/questions/{id}", question.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.answers.length()").value(1))
                    .andExpect(jsonPath("$.answers[0].userId").value(other.getId()));
        }

        @Test
        @DisplayName("다른 질문 경로로는 답변에 접근할 수 없다")
        void 타질문경로_답변접근_404() throws Exception {
            Question another = questionRepository.save(Question.builder()
                    .user(author).title("다른 질문").content("내용").build());

            mockMvc.perform(get("/questions/{qid}/answers/{aid}", another.getId(), answer.getId())
                            .header(HttpHeaders.AUTHORIZATION, authorToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("없는 질문은 404")
        void 없는_질문_404() throws Exception {
            mockMvc.perform(get("/questions/{id}", 999999L)
                            .header(HttpHeaders.AUTHORIZATION, authorToken))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"));
        }
    }
}
