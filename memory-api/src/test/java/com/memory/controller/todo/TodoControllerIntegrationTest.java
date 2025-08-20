package com.memory.controller.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.routine.Routine;
import com.memory.domain.routine.repository.RoutineRepository;
import com.memory.domain.todo.Todo;
import com.memory.domain.todo.repository.TodoRepository;
import com.memory.dto.todo.TodoRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TodoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member testMember;
    private Todo testTodo;
    private Routine testRoutine;
    private String validToken;

    @BeforeEach
    void setUp() {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        String encodedPassword = passwordEncoder.encode("password123");
        testMember = new Member("Test User", "testuser", uniqueEmail, encodedPassword, MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());
        
        testTodo = Todo.create(
            "Test Todo",
            "Test description",
            LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
            testMember
        );
        testTodo = todoRepository.save(testTodo);

        testRoutine = Routine.create(
            "Test Routine",
            "Routine description",
            testMember,
            null
        );
        testRoutine = routineRepository.save(testRoutine);
    }

    @Test
    @DisplayName("할 일 생성 통합 테스트 - 성공")
    void createTodoIntegrationSuccess() throws Exception {
        // Given
        TodoRequest.Create request = new TodoRequest.Create(
            "New Todo",
            "New description",
            LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(14, 0))
        );

        // When & Then
        mockMvc.perform(post("/api/v1/todos")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.title").value("New Todo"))
                .andExpect(jsonPath("$.data.content").value("New description"));
    }

    @Test
    @DisplayName("할 일 생성 실패 - 인증 토큰 없음")
    void createTodoFailNoAuth() throws Exception {
        // Given
        TodoRequest.Create request = new TodoRequest.Create(
            "New Todo", "Description", LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0))
        );

        // When & Then
        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("할 일 수정 통합 테스트")
    void updateTodoIntegrationTest() throws Exception {
        // Given
        TodoRequest.Update request = new TodoRequest.Update(
            "Updated Todo",
            "Updated description",
            LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(16, 0))
        );

        // When & Then
        mockMvc.perform(put("/api/v1/todos/{todoId}", testTodo.getId())
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.title").value("Updated Todo"))
                .andExpect(jsonPath("$.data.content").value("Updated description"));
    }

    @Test
    @DisplayName("할 일 상태 변경 통합 테스트")
    void updateTodoStatusIntegrationTest() throws Exception {
        // Given
        TodoRequest.UpdateStatus request = new TodoRequest.UpdateStatus(true);

        // When & Then
        mockMvc.perform(patch("/api/v1/todos/{todoId}/status", testTodo.getId())
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.completed").value(true));
    }

    @Test
    @DisplayName("할 일 삭제 통합 테스트")
    void deleteTodoIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/todos/{todoId}", testTodo.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("기간별 할 일 조회 통합 테스트")
    void getTodosByDateRangeIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/todos/date-range")
                        .header("Authorization", validToken)
                        .param("startDate", "2025-08-01")
                        .param("endDate", "2025-12-31"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Todo와 루틴 미리보기 조회 통합 테스트")
    void getCombinedTodosIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/todos/combined")
                        .header("Authorization", validToken)
                        .param("startDate", "2025-08-01")
                        .param("endDate", "2025-08-31"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("루틴을 Todo로 변환 통합 테스트")
    void convertRoutineToTodoIntegrationTest() throws Exception {
        // Given
        TodoRequest.ConvertRoutine request = new TodoRequest.ConvertRoutine(
            testRoutine.getId(),
            LocalDate.now().plusDays(1)
        );

        // When & Then
        mockMvc.perform(post("/api/v1/todos/convert-routine")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("해당 날짜에 적용할 수 없는 루틴입니다."));
    }

    @Test
    @DisplayName("할 일 생성 실패 - 잘못된 요청 데이터")
    void createTodoFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/todos")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 할 일 수정 시도")
    void updateNonExistentTodo() throws Exception {
        // Given
        TodoRequest.Update request = new TodoRequest.Update(
            "Updated", "Description", LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 0))
        );

        // When & Then
        mockMvc.perform(put("/api/v1/todos/{todoId}", 99999L)
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}