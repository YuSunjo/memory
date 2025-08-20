package com.memory.service.game;

import com.memory.domain.game.*;
import com.memory.domain.game.repository.GameQuestionRepository;
import com.memory.domain.game.repository.GameSessionRepository;
import com.memory.domain.game.repository.GameSettingRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.game.GameQuestionRequest;
import com.memory.dto.game.response.GameQuestionResponse;
import com.memory.exception.customException.ConflictException;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import com.memory.service.game.factory.GameFactory;
import com.memory.service.game.factory.GameFactoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameQuestionServiceTest {

    private static final MockSettings LENIENT = withSettings().lenient();

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private GameSettingRepository gameSettingRepository;

    @Mock
    private GameQuestionRepository gameQuestionRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GameFactory gameFactory;

    @Mock
    private GameFactoryService gameFactoryService;

    @InjectMocks
    private GameQuestionService gameQuestionService;

    private Member member;
    private Member otherMember;
    private GameSession gameSession;
    private GameSession completedSession;
    private GameSetting gameSetting;
    private GameQuestion gameQuestion;
    private GameQuestion answeredQuestion;
    private GameQuestionRequest.SubmitAnswer submitRequest;
    private GameQuestionResponse questionResponse;

    private final Long memberId = 1L;
    private final Long otherMemberId = 2L;
    private final Long sessionId = 1L;
    private final Long questionId = 1L;
    private final Long settingId = 1L;
    private final GameMode gameMode = GameMode.MY_MEMORIES;
    private final BigDecimal correctLatitude = new BigDecimal("37.5665");
    private final BigDecimal correctLongitude = new BigDecimal("126.9780");
    private final BigDecimal playerLatitude = new BigDecimal("37.5500");
    private final BigDecimal playerLongitude = new BigDecimal("126.9500");

    @BeforeEach
    void setUp() {
        member = new Member("테스트 사용자", "testuser", "test@example.com", "encodedPassword");
        setId(member, memberId);

        otherMember = new Member("다른 사용자", "otheruser", "other@example.com", "encodedPassword");
        setId(otherMember, otherMemberId);

        gameSetting = mock(GameSetting.class, LENIENT);
        when(gameSetting.getId()).thenReturn(settingId);
        when(gameSetting.getGameMode()).thenReturn(gameMode);
        when(gameSetting.getMaxQuestions()).thenReturn(10);
        when(gameSetting.getMaxDistanceForFullScoreKm()).thenReturn(1);

        gameSession = mock(GameSession.class, LENIENT);
        when(gameSession.getId()).thenReturn(sessionId);
        when(gameSession.getGameMode()).thenReturn(gameMode);
        when(gameSession.isOwner(member)).thenReturn(true);
        when(gameSession.isOwner(otherMember)).thenReturn(false);
        when(gameSession.isInProgress()).thenReturn(true);
        when(gameSession.isAnsweredAllQuestions(10)).thenReturn(false);

        completedSession = mock(GameSession.class, LENIENT);
        when(completedSession.getId()).thenReturn(2L);
        when(completedSession.isOwner(member)).thenReturn(true);
        when(completedSession.isInProgress()).thenReturn(false);

        gameQuestion = mock(GameQuestion.class, LENIENT);
        when(gameQuestion.getId()).thenReturn(questionId);
        when(gameQuestion.getGameSession()).thenReturn(gameSession);
        when(gameQuestion.getMemory()).thenReturn(null);
        when(gameQuestion.getQuestionOrder()).thenReturn(1);
        when(gameQuestion.getCorrectLatitude()).thenReturn(correctLatitude);
        when(gameQuestion.getCorrectLongitude()).thenReturn(correctLongitude);
        when(gameQuestion.getCreateDate()).thenReturn(LocalDateTime.now());
        when(gameQuestion.isAnswered()).thenReturn(false);
        when(gameQuestion.isCorrectAnswer(any(Integer.class))).thenReturn(true);

        answeredQuestion = mock(GameQuestion.class, LENIENT);
        when(answeredQuestion.getId()).thenReturn(2L);
        when(answeredQuestion.isAnswered()).thenReturn(true);

        submitRequest = new GameQuestionRequest.SubmitAnswer(playerLatitude, playerLongitude, 30);

        // GameQuestionResponse는 실제 객체 생성이 필요함 (null로 설정)
        questionResponse = null;
    }

    private void setId(Object entity, Long id) {
        try {
            Field idField = null;
            Class<?> clazz = entity.getClass();
            
            // 상위 클래스까지 탐색하여 id 필드 찾기
            while (clazz != null && idField == null) {
                try {
                    idField = clazz.getDeclaredField("id");
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            
            if (idField == null) {
                throw new NoSuchFieldException("id field not found in class hierarchy");
            }
            
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @Test
    @DisplayName("다음 문제 조회 성공 테스트")
    void getNextQuestionSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));
        when(gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode))
                .thenReturn(Optional.of(gameSetting));
        when(gameQuestionRepository.findNextQuestionOrder(gameSession)).thenReturn(6);
        GameQuestionResponse mockResponse = mock(GameQuestionResponse.class);
        when(gameFactory.getGameService(gameMode)).thenReturn(gameFactoryService);
        when(gameFactoryService.getNextQuestion(member, gameSession, gameSetting, 5))
                .thenReturn(mockResponse);

        // When
        GameQuestionResponse response = gameQuestionService.getNextQuestion(memberId, sessionId);

        // Then
        assertNotNull(response);
        assertEquals(mockResponse, response);

        verify(memberRepository).findMemberById(memberId);
        verify(gameSessionRepository).findGameSessionById(sessionId);
        verify(gameSettingRepository).findByGameModeAndIsActiveTrue(gameMode);
        verify(gameQuestionRepository).findNextQuestionOrder(gameSession);
        verify(gameFactory).getGameService(gameMode);
        verify(gameFactoryService).getNextQuestion(member, gameSession, gameSetting, 5);
    }

    @Test
    @DisplayName("다음 문제 조회 실패 테스트 - 존재하지 않는 회원")
    void getNextQuestionFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> gameQuestionService.getNextQuestion(memberId, sessionId));

        assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(gameSessionRepository, never()).findGameSessionById(any());
    }

    @Test
    @DisplayName("다음 문제 조회 실패 테스트 - 존재하지 않는 게임 세션")
    void getNextQuestionFailSessionNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> gameQuestionService.getNextQuestion(memberId, sessionId));

        assertEquals("존재하지 않는 게임 세션입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("다음 문제 조회 실패 테스트 - 권한 없음")
    void getNextQuestionFailNoPermission() {
        // Given
        when(memberRepository.findMemberById(otherMemberId)).thenReturn(Optional.of(otherMember));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> gameQuestionService.getNextQuestion(otherMemberId, sessionId));

        assertEquals("본인의 게임 세션만 조회할 수 있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("다음 문제 조회 실패 테스트 - 진행중이지 않은 게임")
    void getNextQuestionFailNotInProgress() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(completedSession));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> gameQuestionService.getNextQuestion(memberId, sessionId));

        assertEquals("진행 중인 게임 세션이 아닙니다.", exception.getMessage());
    }

    @Test
    @DisplayName("다음 문제 조회 실패 테스트 - 게임 설정 없음")
    void getNextQuestionFailGameSettingNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));
        when(gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode))
                .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> gameQuestionService.getNextQuestion(memberId, sessionId));

        assertEquals("해당 게임 모드의 설정을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("다음 문제 조회 실패 테스트 - 모든 문제 완료")
    void getNextQuestionFailAllQuestionsCompleted() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));
        when(gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode))
                .thenReturn(Optional.of(gameSetting));
        when(gameQuestionRepository.findNextQuestionOrder(gameSession)).thenReturn(12);

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> gameQuestionService.getNextQuestion(memberId, sessionId));

        assertEquals("모든 문제가 완료되었습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("답안 제출 성공 테스트")
    void submitAnswerSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));
        when(gameQuestionRepository.findByIdAndGameSession(questionId, gameSession))
                .thenReturn(Optional.of(gameQuestion));
        when(gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode))
                .thenReturn(Optional.of(gameSetting));

        // When
        GameQuestionResponse response = gameQuestionService.submitAnswer(memberId, sessionId, questionId, submitRequest);

        // Then
        assertNotNull(response);

        verify(memberRepository).findMemberById(memberId);
        verify(gameSessionRepository).findGameSessionById(sessionId);
        verify(gameQuestionRepository).findByIdAndGameSession(questionId, gameSession);
        verify(gameQuestion).submitAnswer(eq(playerLatitude), eq(playerLongitude), any(BigDecimal.class), anyInt(), eq(30));
        verify(gameSession).updateScore(anyInt());
        verify(gameSession).incrementCorrectAnswers();
    }

    @Test
    @DisplayName("답안 제출 성공 테스트 - 게임 완료")
    void submitAnswerSuccessGameCompleted() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));
        when(gameQuestionRepository.findByIdAndGameSession(questionId, gameSession))
                .thenReturn(Optional.of(gameQuestion));
        when(gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode))
                .thenReturn(Optional.of(gameSetting));
        when(gameSession.isAnsweredAllQuestions(10)).thenReturn(true);

        // When
        GameQuestionResponse response = gameQuestionService.submitAnswer(memberId, sessionId, questionId, submitRequest);

        // Then
        assertNotNull(response);
        verify(gameSession).completeGame();
    }

    @Test
    @DisplayName("답안 제출 실패 테스트 - 존재하지 않는 회원")
    void submitAnswerFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> gameQuestionService.submitAnswer(memberId, sessionId, questionId, submitRequest));

        assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("답안 제출 실패 테스트 - 존재하지 않는 게임 세션")
    void submitAnswerFailSessionNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> gameQuestionService.submitAnswer(memberId, sessionId, questionId, submitRequest));

        assertEquals("존재하지 않는 게임 세션입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("답안 제출 실패 테스트 - 권한 없음")
    void submitAnswerFailNoPermission() {
        // Given
        when(memberRepository.findMemberById(otherMemberId)).thenReturn(Optional.of(otherMember));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> gameQuestionService.submitAnswer(otherMemberId, sessionId, questionId, submitRequest));

        assertEquals("본인의 게임 세션만 답안 제출이 가능합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("답안 제출 실패 테스트 - 진행중이지 않은 게임")
    void submitAnswerFailNotInProgress() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(completedSession));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> gameQuestionService.submitAnswer(memberId, sessionId, questionId, submitRequest));

        assertEquals("진행 중인 게임 세션이 아닙니다.", exception.getMessage());
    }

    @Test
    @DisplayName("답안 제출 실패 테스트 - 존재하지 않는 문제")
    void submitAnswerFailQuestionNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));
        when(gameQuestionRepository.findByIdAndGameSession(questionId, gameSession))
                .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> gameQuestionService.submitAnswer(memberId, sessionId, questionId, submitRequest));

        assertEquals("해당 게임 세션에서 문제를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("답안 제출 실패 테스트 - 이미 답안이 제출된 문제")
    void submitAnswerFailAlreadyAnswered() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));
        when(gameQuestionRepository.findByIdAndGameSession(questionId, gameSession))
                .thenReturn(Optional.of(answeredQuestion));

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> gameQuestionService.submitAnswer(memberId, sessionId, questionId, submitRequest));

        assertEquals("이미 답안이 제출된 문제입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("답안 제출 테스트 - 오답인 경우")
    void submitAnswerWrongAnswer() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));
        when(gameQuestionRepository.findByIdAndGameSession(questionId, gameSession))
                .thenReturn(Optional.of(gameQuestion));
        when(gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode))
                .thenReturn(Optional.of(gameSetting));
        when(gameQuestion.isCorrectAnswer(any(Integer.class))).thenReturn(false);

        // When
        GameQuestionResponse response = gameQuestionService.submitAnswer(memberId, sessionId, questionId, submitRequest);

        // Then
        assertNotNull(response);
        verify(gameSession).updateScore(anyInt());
        verify(gameSession, never()).incrementCorrectAnswers();
    }
}