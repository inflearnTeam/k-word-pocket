package com.example.kwordpocket.faq;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.kwordpocket.common.config.JwtUtil;
import com.example.kwordpocket.faq.entity.Faq;
import com.example.kwordpocket.faq.repository.FaqRepository;
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
@DisplayName("FAQ API")
class FaqApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired FaqRepository faqRepository;
    @Autowired JwtUtil jwtUtil;
    @PersistenceContext EntityManager entityManager;

    private String userToken;
    private String adminToken;
    private Faq faq;

    @BeforeEach
    void setUp() {
        userToken = tokenOf(createUser("user@test.com", Role.ROLE_USER));
        adminToken = tokenOf(createUser("admin@test.com", Role.ROLE_ADMIN));
        faq = faqRepository.save(new Faq("등록된 질문", "등록된 답변"));

        entityManager.flush();
        entityManager.clear();
    }

    private User createUser(String email, Role role) {
        return userRepository.save(new User(email, "encoded-password", role));
    }

    private String tokenOf(User user) {
        return "Bearer " + jwtUtil.createToken(user.getId(), user.getEmail(), user.getRole());
    }

    private static String faqBody(String question, String answer) {
        return """
                {"question":"%s","answer":"%s"}""".formatted(question, answer);
    }

    @Nested
    @DisplayName("공개 조회 - 인증 불필요")
    class PublicRead {

        @Test
        @DisplayName("인증 없이 목록을 조회할 수 있다")
        void 미인증_목록_200() throws Exception {
            mockMvc.perform(get("/faqs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("목록은 페이지 정보를 함께 반환한다")
        void 목록_페이징_봉투() throws Exception {
            mockMvc.perform(get("/faqs").param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page.size").value(5))
                    .andExpect(jsonPath("$.page.totalElements").exists());
        }

        @Test
        @DisplayName("인증 없이 단건을 조회할 수 있고 생성·수정 시각이 포함된다")
        void 미인증_단건_200() throws Exception {
            mockMvc.perform(get("/faqs/{id}", faq.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.question").value("등록된 질문"))
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.updatedAt").exists());
        }

        @Test
        @DisplayName("없는 FAQ는 404")
        void 없는_FAQ_404() throws Exception {
            mockMvc.perform(get("/faqs/{id}", 999999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"));
        }

        @Test
        @DisplayName("존재하지 않는 정렬 기준은 500이 아니라 400")
        void 잘못된_정렬_400() throws Exception {
            mockMvc.perform(get("/faqs").param("sort", "asdf"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
        }
    }

    @Nested
    @DisplayName("관리자 권한 - ROLE_ADMIN 만 쓰기 가능")
    class AdminAuthorization {

        @Test
        @DisplayName("인증 없이는 생성할 수 없다")
        void 미인증_생성_401() throws Exception {
            mockMvc.perform(post("/admin/faqs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(faqBody("새 질문", "새 답변")))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("일반 사용자는 생성할 수 없다")
        void 일반사용자_생성_403() throws Exception {
            mockMvc.perform(post("/admin/faqs")
                            .header(HttpHeaders.AUTHORIZATION, userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(faqBody("새 질문", "새 답변")))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("일반 사용자는 수정할 수 없다")
        void 일반사용자_수정_403() throws Exception {
            mockMvc.perform(put("/admin/faqs/{id}", faq.getId())
                            .header(HttpHeaders.AUTHORIZATION, userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(faqBody("수정 질문", "수정 답변")))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("일반 사용자는 삭제할 수 없다")
        void 일반사용자_삭제_403() throws Exception {
            mockMvc.perform(delete("/admin/faqs/{id}", faq.getId())
                            .header(HttpHeaders.AUTHORIZATION, userToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("관리자는 생성할 수 있고 201과 Location 을 받는다")
        void 관리자_생성_201() throws Exception {
            mockMvc.perform(post("/admin/faqs")
                            .header(HttpHeaders.AUTHORIZATION, adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(faqBody("새 질문", "새 답변")))
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, startsWith("/faqs/")))
                    .andExpect(jsonPath("$.question").value("새 질문"));
        }

        @Test
        @DisplayName("관리자는 수정할 수 있다")
        void 관리자_수정_200() throws Exception {
            mockMvc.perform(put("/admin/faqs/{id}", faq.getId())
                            .header(HttpHeaders.AUTHORIZATION, adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(faqBody("수정 질문", "수정 답변")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.answer").value("수정 답변"));
        }

        @Test
        @DisplayName("관리자는 삭제할 수 있고 204를 받는다")
        void 관리자_삭제_204() throws Exception {
            mockMvc.perform(delete("/admin/faqs/{id}", faq.getId())
                            .header(HttpHeaders.AUTHORIZATION, adminToken))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/faqs/{id}", faq.getId()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("입력 검증")
    class InputValidation {

        @Test
        @DisplayName("질문이 비면 400")
        void 질문_공백_400() throws Exception {
            mockMvc.perform(post("/admin/faqs")
                            .header(HttpHeaders.AUTHORIZATION, adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(faqBody("  ", "답변")))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("질문이 200자를 넘으면 400")
        void 질문_초과_400() throws Exception {
            mockMvc.perform(post("/admin/faqs")
                            .header(HttpHeaders.AUTHORIZATION, adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(faqBody("가".repeat(201), "답변")))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("답변이 255자를 넘어도 저장된다")
        void 답변_장문_201() throws Exception {
            mockMvc.perform(post("/admin/faqs")
                            .header(HttpHeaders.AUTHORIZATION, adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(faqBody("장문 답변 질문", "가".repeat(1000))))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("답변이 16000자를 넘으면 400")
        void 답변_초과_400() throws Exception {
            mockMvc.perform(post("/admin/faqs")
                            .header(HttpHeaders.AUTHORIZATION, adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(faqBody("질문", "가".repeat(16001))))
                    .andExpect(status().isBadRequest());
        }
    }
}
