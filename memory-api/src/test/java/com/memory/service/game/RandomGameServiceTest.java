package com.memory.service.game;

import com.memory.domain.cities.Cities;
import com.memory.domain.cities.repository.CitiesRepository;
import com.memory.domain.game.*;
import com.memory.domain.game.repository.GameQuestionRepository;
import com.memory.domain.member.Member;
import com.memory.dto.game.GameSessionRequest;
import com.memory.dto.game.response.GameQuestionResponse;
import com.memory.exception.customException.NotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RandomGameServiceTest {

    private static final MockSettings LENIENT = withSettings().lenient();

    @Mock
    private CitiesRepository citiesRepository;

    @Mock
    private GameQuestionRepository gameQuestionRepository;

    @InjectMocks
    private RandomGameService randomGameService;

    private Member member;
    private GameSession gameSession;
    private GameSetting gameSetting;
    private GameQuestion gameQuestion;
    private Cities cities;
    private GameSessionRequest.Create createRequest;

    private final Long memberId = 1L;
    private final Long sessionId = 1L;
    private final Long settingId = 1L;
    private final Long questionId = 1L;
    private final String cityName = "서울";
    private final Double cityLatitude = 37.5665;
    private final Double cityLongitude = 126.9780;

    @BeforeEach
    void setUp() {
        member = new Member("테스트 사용자", "testuser", "test@example.com", "encodedPassword");
        setId(member, memberId);

        gameSession = mock(GameSession.class, LENIENT);
        when(gameSession.getId()).thenReturn(sessionId);

        gameSetting = mock(GameSetting.class, LENIENT);
        when(gameSetting.getId()).thenReturn(settingId);
        when(gameSetting.getGameMode()).thenReturn(GameMode.RANDOM);

        cities = mock(Cities.class, LENIENT);
        when(cities.getName()).thenReturn(cityName);
        when(cities.getLatitude()).thenReturn(cityLatitude);
        when(cities.getLongitude()).thenReturn(cityLongitude);

        gameQuestion = mock(GameQuestion.class, LENIENT);
        when(gameQuestion.getId()).thenReturn(questionId);
        when(gameQuestion.getGameSession()).thenReturn(gameSession);
        when(gameQuestion.getMemory()).thenReturn(null);
        when(gameQuestion.getQuestionOrder()).thenReturn(1);
        when(gameQuestion.getCorrectLatitude()).thenReturn(new BigDecimal("37.5665"));
        when(gameQuestion.getCorrectLongitude()).thenReturn(new BigDecimal("126.9780"));
        when(gameQuestion.getCreateDate()).thenReturn(LocalDateTime.now());

        createRequest = new GameSessionRequest.Create(GameMode.RANDOM);
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
    @DisplayName("랜덤 게임 세션 생성 성공 테스트")
    void createGameSessionSuccess() {
        // When
        GameSession result = randomGameService.createGameSession(member, gameSetting, createRequest);

        // Then
        assertNotNull(result);
        assertEquals(GameMode.RANDOM, result.getGameMode());
        assertEquals(member, result.getMember());
        assertEquals(GameSessionStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    @DisplayName("다음 문제 생성 성공 테스트")
    void getNextQuestionSuccess() {
        // Given
        when(citiesRepository.findRandomCities()).thenReturn(Optional.of(cities));
        when(gameQuestionRepository.save(any(GameQuestion.class))).thenReturn(gameQuestion);

        // When
        GameQuestionResponse response = randomGameService.getNextQuestion(member, gameSession, gameSetting, 1);

        // Then
        assertNotNull(response);

        verify(citiesRepository).findRandomCities();
        verify(gameQuestionRepository).save(any(GameQuestion.class));
        verify(gameSession).addGameQuestion(any(GameQuestion.class));
    }

    @Test
    @DisplayName("다음 문제 생성 실패 테스트 - 도시 데이터 없음")
    void getNextQuestionFailNoCitiesData() {
        // Given
        when(citiesRepository.findRandomCities()).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> randomGameService.getNextQuestion(member, gameSession, gameSetting, 1));

        assertEquals("도시 데이터가 비어 있습니다.", exception.getMessage());
        verify(citiesRepository).findRandomCities();
        verify(gameQuestionRepository, never()).save(any());
        verify(gameSession, never()).addGameQuestion(any());
    }

    @Test
    @DisplayName("여러 문제 생성 테스트")
    void getMultipleQuestionsSuccess() {
        // Given
        Cities seoulCities = mock(Cities.class);
        when(seoulCities.getName()).thenReturn("서울");
        when(seoulCities.getLatitude()).thenReturn(37.5665);
        when(seoulCities.getLongitude()).thenReturn(126.9780);

        Cities busanCities = mock(Cities.class);
        when(busanCities.getName()).thenReturn("부산");
        when(busanCities.getLatitude()).thenReturn(35.1796);
        when(busanCities.getLongitude()).thenReturn(129.0756);

        when(citiesRepository.findRandomCities())
                .thenReturn(Optional.of(seoulCities))
                .thenReturn(Optional.of(busanCities));
        when(gameQuestionRepository.save(any(GameQuestion.class))).thenReturn(gameQuestion);

        // When
        GameQuestionResponse response1 = randomGameService.getNextQuestion(member, gameSession, gameSetting, 1);
        GameQuestionResponse response2 = randomGameService.getNextQuestion(member, gameSession, gameSetting, 2);

        // Then
        assertNotNull(response1);
        assertNotNull(response2);

        verify(citiesRepository, times(2)).findRandomCities();
        verify(gameQuestionRepository, times(2)).save(any(GameQuestion.class));
        verify(gameSession, times(2)).addGameQuestion(any(GameQuestion.class));
    }

    @Test
    @DisplayName("게임 세션 타겟 멤버 확인 테스트")
    void gameSessionTargetMemberTest() {
        // When
        GameSession result = randomGameService.createGameSession(member, gameSetting, createRequest);

        // Then
        assertNotNull(result);
        assertEquals(member, result.getMember());
        assertNull(result.getTargetMember()); // 랜덤 게임은 타겟 멤버가 없음
    }

    @Test
    @DisplayName("게임 세션 초기 상태 확인 테스트")
    void gameSessionInitialStateTest() {
        // When
        GameSession result = randomGameService.createGameSession(member, gameSetting, createRequest);

        // Then
        assertNotNull(result);
        assertEquals(GameSessionStatus.IN_PROGRESS, result.getStatus());
        assertEquals(0, result.getTotalScore());
        assertEquals(0, result.getCorrectAnswers());
        assertNotNull(result.getStartTime());
        assertNull(result.getEndTime());
    }
}