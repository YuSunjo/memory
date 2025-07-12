package com.memory.service.game;

import com.memory.domain.cities.Cities;
import com.memory.domain.cities.repository.CitiesRepository;
import com.memory.domain.game.*;
import com.memory.domain.game.repository.GameQuestionRepository;
import com.memory.domain.member.Member;
import com.memory.dto.game.GameSessionRequest;
import com.memory.dto.game.response.GameQuestionResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.service.game.factory.GameFactoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.memory.domain.game.GameSession.gameSessionInit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RandomGameService implements GameFactoryService {

    private final CitiesRepository citiesRepository;
    private final GameQuestionRepository gameQuestionRepository;

    @Override
    public GameSession createGameSession(Member member, GameSetting gameSetting, GameSessionRequest.Create request) {
        GameSession gameSession = gameSessionInit(member, GameMode.RANDOM);
        
        log.info("랜덤 게임 세션 생성 완료 - memberId: {}, sessionId: {}", 
                member.getId(), gameSession.getId());
        
        return gameSession;
    }

    @Override
    public GameQuestionResponse getNextQuestion(Member member, GameSession gameSession, GameSetting gameSetting, Integer nextOrder) {
        // 랜덤 위치 생성 (한국 내 좌표)
        RandomLocation randomLocation = generateRandomWorldLocation();

        GameQuestion gameQuestion = new GameQuestion(
                gameSession,
                null, // Memory 없음
                nextOrder,
                randomLocation.latitude(),
                randomLocation.longitude(),
                randomLocation.locationName()
        );

        GameQuestion savedQuestion = gameQuestionRepository.save(gameQuestion);

        gameSession.addGameQuestion(savedQuestion);

        return GameQuestionResponse.forQuestion(savedQuestion, null);
    }

    private RandomLocation generateRandomWorldLocation() {

        Cities cities = citiesRepository.findRandomCities()
                .orElseThrow(() -> new NotFoundException("도시 데이터가 비어 있습니다."));

        return new RandomLocation(
                BigDecimal.valueOf(cities.getLatitude()).setScale(8, RoundingMode.HALF_UP),
                BigDecimal.valueOf(cities.getLongitude()).setScale(8, RoundingMode.HALF_UP),
                cities.getName()
        );
    }

    private record RandomLocation(BigDecimal latitude, BigDecimal longitude, String locationName) {}

}
