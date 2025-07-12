package com.memory.service.game;

import com.memory.domain.cities.Cities;
import com.memory.domain.cities.repository.CitiesRepository;
import com.memory.domain.game.*;
import com.memory.domain.game.repository.GameQuestionRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.dto.game.GameSessionRequest;
import com.memory.dto.game.response.GameQuestionResponse;
import com.memory.exception.customException.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RandomGameService 테스트")
class RandomGameServiceTest {

    @Mock
    private CitiesRepository citiesRepository;

    @Mock
    private GameQuestionRepository gameQuestionRepository;

    @InjectMocks
    private RandomGameService randomGameService;

    @Test
    @DisplayName("랜덤 게임 세션이 정상적으로 생성된다")
    void createGameSession_Success() {
        // given
        Member member = createTestMember();
        GameSetting gameSetting = createTestGameSetting();
        GameSessionRequest.Create request = new GameSessionRequest.Create(GameMode.RANDOM);

        // when
        GameSession result = randomGameService.createGameSession(member, gameSetting, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getGameMode()).isEqualTo(GameMode.RANDOM);
    }

    @Test
    @DisplayName("다음 문제를 정상적으로 생성한다")
    void getNextQuestion_Success() {
        // given
        Member member = createTestMember();
        GameSession gameSession = GameSession.gameSessionInit(member, GameMode.RANDOM);
        GameSetting gameSetting = createTestGameSetting();
        Integer nextOrder = 1;

        Cities testCity = createTestCities();
        GameQuestion savedQuestion = createTestGameQuestion(gameSession, nextOrder);

        given(citiesRepository.findRandomCities()).willReturn(Optional.of(testCity));
        given(gameQuestionRepository.save(any(GameQuestion.class))).willReturn(savedQuestion);

        // when
        GameQuestionResponse result = randomGameService.getNextQuestion(member, gameSession, gameSetting, nextOrder);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedQuestion.getId());
        assertThat(result.getQuestionOrder()).isEqualTo(nextOrder);
        assertThat(result.getCorrectLatitude()).isNull(); // 문제 단계에서는 정답이 숨겨짐
        assertThat(result.getCorrectLongitude()).isNull();
        assertThat(result.getCorrectLocationName()).isNull();

        verify(citiesRepository).findRandomCities();
        verify(gameQuestionRepository).save(any(GameQuestion.class));
    }

    @Test
    @DisplayName("도시 데이터가 없으면 예외가 발생한다")
    void getNextQuestion_NoCitiesData_ThrowsException() {
        // given
        Member member = createTestMember();
        GameSession gameSession = GameSession.gameSessionInit(member, GameMode.RANDOM);
        GameSetting gameSetting = createTestGameSetting();
        Integer nextOrder = 1;

        given(citiesRepository.findRandomCities()).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> randomGameService.getNextQuestion(member, gameSession, gameSetting, nextOrder))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("도시 데이터가 비어 있습니다.");
    }

    private Member createTestMember() {
        return new Member("테스트 사용자", "testNick", "test@example.com", "password", MemberType.MEMBER);
    }

    private GameSetting createTestGameSetting() {
        // GameSetting은 기본 생성자만 있으므로 테스트에서 실제로 사용되지 않으므로 null로 처리
        return null;
    }

    private Cities createTestCities() {
        return Cities.builder()
                .name("서울")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();
    }

    private GameQuestion createTestGameQuestion(GameSession gameSession, Integer order) {
        return new GameQuestion(
                gameSession,
                null,
                order,
                new BigDecimal("37.5665"),
                new BigDecimal("126.9780"),
                "서울"
        ) {
            @Override
            public Long getId() {
                return 1L;
            }
        };
    }
}
