package com.memory.service.game;

import com.memory.domain.game.*;
import com.memory.domain.game.repository.GameSessionRepository;
import com.memory.domain.game.repository.GameSettingRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.game.GameSessionRequest;
import com.memory.dto.game.response.GameSessionResponse;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameSessionServiceTest {

    private static final MockSettings LENIENT = withSettings().lenient();

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private GameSettingRepository gameSettingRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GameFactory gameFactory;

    @Mock
    private GameFactoryService gameFactoryService;

    @InjectMocks
    private GameSessionService gameSessionService;

    private Member member;
    private Member targetMember;
    private GameSession gameSession;
    private GameSession inProgressSession;
    private GameSetting gameSetting;
    private GameSessionRequest.Create createRequest;
    private GameSessionRequest.GetList getListRequest;

    private final Long memberId = 1L;
    private final Long targetMemberId = 2L;
    private final Long sessionId = 1L;
    private final Long settingId = 1L;
    private final GameMode gameMode = GameMode.MY_MEMORIES;

    @BeforeEach
    void setUp() {
        member = new Member("테스트 사용자", "testuser", "test@example.com", "encodedPassword");
        setId(member, memberId);

        targetMember = new Member("타겟 사용자", "targetuser", "target@example.com", "encodedPassword");
        setId(targetMember, targetMemberId);

        gameSetting = mock(GameSetting.class, LENIENT);
        when(gameSetting.getId()).thenReturn(settingId);
        when(gameSetting.getGameMode()).thenReturn(gameMode);

        gameSession = mock(GameSession.class, LENIENT);
        when(gameSession.getId()).thenReturn(sessionId);
        when(gameSession.getMember()).thenReturn(member);
        when(gameSession.getTargetMember()).thenReturn(targetMember);
        when(gameSession.getGameMode()).thenReturn(gameMode);
        when(gameSession.getStatus()).thenReturn(GameSessionStatus.COMPLETED);
        when(gameSession.getTotalScore()).thenReturn(100);
        when(gameSession.getTotalQuestions()).thenReturn(10);
        when(gameSession.getCorrectAnswers()).thenReturn(8);
        when(gameSession.getAccuracy()).thenReturn(80.0);
        when(gameSession.getStartTime()).thenReturn(LocalDateTime.now().minusHours(1));
        when(gameSession.getEndTime()).thenReturn(LocalDateTime.now());
        when(gameSession.getCreateDate()).thenReturn(LocalDateTime.now().minusHours(1));
        when(gameSession.isInProgress()).thenReturn(false);

        inProgressSession = mock(GameSession.class, LENIENT);
        when(inProgressSession.getId()).thenReturn(2L);
        when(inProgressSession.getMember()).thenReturn(member);
        when(inProgressSession.getStatus()).thenReturn(GameSessionStatus.IN_PROGRESS);
        when(inProgressSession.isInProgress()).thenReturn(true);

        createRequest = new GameSessionRequest.Create(gameMode);
        getListRequest = new GameSessionRequest.GetList(gameMode, null, 10);
    }

    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @Test
    @DisplayName("게임 세션 생성 성공 테스트")
    void createGameSessionSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());
        when(gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode))
                .thenReturn(Optional.of(gameSetting));
        when(gameFactory.getGameService(gameMode)).thenReturn(gameFactoryService);
        when(gameFactoryService.createGameSession(member, gameSetting, createRequest))
                .thenReturn(gameSession);
        when(gameSessionRepository.save(gameSession)).thenReturn(gameSession);

        // When
        GameSessionResponse response = gameSessionService.createGameSession(memberId, createRequest);

        // Then
        assertNotNull(response);
        assertEquals(sessionId, response.getId());
        assertEquals(memberId, response.getMemberId());
        assertEquals(gameMode, response.getGameMode());

        verify(memberRepository).findMemberById(memberId);
        verify(gameSessionRepository).findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS);
        verify(gameSettingRepository).findByGameModeAndIsActiveTrue(gameMode);
        verify(gameFactory).getGameService(gameMode);
        verify(gameFactoryService).createGameSession(member, gameSetting, createRequest);
        verify(gameSessionRepository).save(gameSession);
    }

    @Test
    @DisplayName("게임 세션 생성 실패 테스트 - 존재하지 않는 회원")
    void createGameSessionFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> gameSessionService.createGameSession(memberId, createRequest));

        assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(gameSessionRepository, never()).findByMemberAndStatus(any(), any());
    }

    @Test
    @DisplayName("게임 세션 생성 실패 테스트 - 진행중인 게임 존재")
    void createGameSessionFailGameInProgress() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS))
                .thenReturn(Optional.of(inProgressSession));

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> gameSessionService.createGameSession(memberId, createRequest));

        assertEquals("이미 진행중인 게임이 있습니다. 먼저 완료하거나 포기해주세요.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(gameSessionRepository).findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS);
        verify(gameSettingRepository, never()).findByGameModeAndIsActiveTrue(any());
    }

    @Test
    @DisplayName("게임 세션 생성 실패 테스트 - 게임 설정 없음")
    void createGameSessionFailGameSettingNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());
        when(gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode))
                .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> gameSessionService.createGameSession(memberId, createRequest));

        assertEquals("해당 게임 모드의 설정을 찾을 수 없습니다.", exception.getMessage());
        verify(gameSettingRepository).findByGameModeAndIsActiveTrue(gameMode);
    }

    @Test
    @DisplayName("게임 세션 조회 성공 테스트")
    void findGameSessionByIdSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));
        when(gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode))
                .thenReturn(Optional.of(gameSetting));

        // When
        GameSessionResponse response = gameSessionService.findGameSessionById(memberId, sessionId);

        // Then
        assertNotNull(response);
        assertEquals(sessionId, response.getId());
        assertEquals(memberId, response.getMemberId());

        verify(memberRepository).findMemberById(memberId);
        verify(gameSessionRepository).findGameSessionById(sessionId);
        verify(gameSettingRepository).findByGameModeAndIsActiveTrue(gameMode);
    }

    @Test
    @DisplayName("게임 세션 조회 실패 테스트 - 존재하지 않는 세션")
    void findGameSessionByIdFailSessionNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameSessionService.findGameSessionById(memberId, sessionId));

        assertEquals("존재하지 않는 게임 세션입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("게임 세션 조회 실패 테스트 - 권한 없음")
    void findGameSessionByIdFailNoPermission() {
        // Given
        Member otherMember = new Member("다른 사용자", "other", "other@example.com", "encodedPassword");
        setId(otherMember, 999L);
        
        when(memberRepository.findMemberById(999L)).thenReturn(Optional.of(otherMember));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> gameSessionService.findGameSessionById(999L, sessionId));

        assertEquals("본인의 게임 세션만 조회할 수 있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("진행중인 게임 세션 조회 성공 테스트")
    void findProgressGameSessionSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS))
                .thenReturn(Optional.of(inProgressSession));
        when(inProgressSession.getGameMode()).thenReturn(gameMode);
        when(gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode))
                .thenReturn(Optional.of(gameSetting));

        // When
        GameSessionResponse response = gameSessionService.findProgressGameSession(memberId);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(gameSessionRepository).findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("진행중인 게임 세션 조회 실패 테스트 - 진행중인 게임 없음")
    void findProgressGameSessionFailNoProgressGame() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> gameSessionService.findProgressGameSession(memberId));

        assertEquals("현재 진행중인 게임이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원별 게임 세션 목록 조회 성공 테스트")
    void findGameSessionsByMemberSuccess() {
        // Given
        List<GameSession> gameSessions = Arrays.asList(gameSession, inProgressSession);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findByMemberAndGameMode(member, gameMode, null, 10))
                .thenReturn(gameSessions);
        when(gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode))
                .thenReturn(Optional.of(gameSetting));

        // When
        List<GameSessionResponse> responses = gameSessionService.findGameSessionsByMember(memberId, getListRequest);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        verify(memberRepository).findMemberById(memberId);
        verify(gameSessionRepository).findByMemberAndGameMode(member, gameMode, null, 10);
    }

    @Test
    @DisplayName("회원별 게임 세션 목록 조회 테스트 - 빈 목록")
    void findGameSessionsByMemberEmptyList() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findByMemberAndGameMode(member, gameMode, null, 10))
                .thenReturn(Collections.emptyList());

        // When
        List<GameSessionResponse> responses = gameSessionService.findGameSessionsByMember(memberId, getListRequest);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("게임 세션 포기 성공 테스트")
    void giveUpGameSessionSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(inProgressSession));

        // When
        assertDoesNotThrow(() -> gameSessionService.giveUpGameSession(memberId, sessionId));

        // Then
        verify(memberRepository).findMemberById(memberId);
        verify(gameSessionRepository).findGameSessionById(sessionId);
        verify(inProgressSession).giveUpGameSession();
    }

    @Test
    @DisplayName("게임 세션 포기 실패 테스트 - 존재하지 않는 회원")
    void giveUpGameSessionFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> gameSessionService.giveUpGameSession(memberId, sessionId));

        assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("게임 세션 포기 실패 테스트 - 존재하지 않는 세션")
    void giveUpGameSessionFailSessionNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> gameSessionService.giveUpGameSession(memberId, sessionId));

        assertEquals("존재하지 않는 게임 세션입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("게임 세션 포기 실패 테스트 - 권한 없음")
    void giveUpGameSessionFailNoPermission() {
        // Given
        Member otherMember = new Member("다른 사용자", "other", "other@example.com", "encodedPassword");
        setId(otherMember, 999L);
        
        when(memberRepository.findMemberById(999L)).thenReturn(Optional.of(otherMember));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> gameSessionService.giveUpGameSession(999L, sessionId));

        assertEquals("본인의 게임 세션만 포기할 수 있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("게임 세션 포기 실패 테스트 - 진행중이지 않은 게임")
    void giveUpGameSessionFailNotInProgress() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(gameSessionRepository.findGameSessionById(sessionId)).thenReturn(Optional.of(gameSession));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> gameSessionService.giveUpGameSession(memberId, sessionId));

        assertEquals("진행중인 게임만 포기할 수 있습니다.", exception.getMessage());
    }
}