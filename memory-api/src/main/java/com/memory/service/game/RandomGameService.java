package com.memory.service.game;

import com.memory.domain.game.*;
import com.memory.domain.game.repository.GameQuestionRepository;
import com.memory.domain.member.Member;
import com.memory.dto.game.GameSessionRequest;
import com.memory.dto.game.response.GameQuestionResponse;
import com.memory.service.game.factory.GameFactoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static com.memory.domain.game.GameSession.gameSessionInit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RandomGameService implements GameFactoryService {

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
        Random random = new Random();
        // 전세계 랜덤 좌표 생성
        // 위도: -90.0 ~ 90.0 (남극 ~ 북극)
        // 경도: -180.0 ~ 180.0 (서경 ~ 동경)
        double minLat = -90.0;
        double maxLat = 90.0;
        double minLon = -180.0;
        double maxLon = 180.0;

        double latitude = minLat + (maxLat - minLat) * random.nextDouble();
        double longitude = minLon + (maxLon - minLon) * random.nextDouble();

        return new RandomLocation(
                BigDecimal.valueOf(latitude).setScale(8, RoundingMode.HALF_UP),
                BigDecimal.valueOf(longitude).setScale(8, RoundingMode.HALF_UP),
                "랜덤 위치 " + random.nextInt(10000)
        );
    }

    private record RandomLocation(BigDecimal latitude, BigDecimal longitude, String locationName) {}

}
