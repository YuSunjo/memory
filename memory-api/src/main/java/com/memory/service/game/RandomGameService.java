package com.memory.service.game;

import com.memory.domain.game.*;
import com.memory.domain.game.repository.GameQuestionRepository;
import com.memory.domain.game.repository.GameSessionRepository;
import com.memory.domain.game.repository.GameSettingRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.game.GameSessionRequest;
import com.memory.dto.game.response.GameQuestionResponse;
import com.memory.exception.customException.ConflictException;
import com.memory.exception.customException.NotFoundException;
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

    private final MemberRepository memberRepository;
    private final GameSessionRepository gameSessionRepository;
    private final GameSettingRepository gameSettingRepository;
    private final GameQuestionRepository gameQuestionRepository;

    @Override
    public GameSession createGameSession(Long memberId, GameSessionRequest.Create request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        gameSessionRepository.findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS)
                .ifPresent(session -> {
                    throw new ConflictException("이미 진행중인 게임이 있습니다. 먼저 완료하거나 포기해주세요.");
                });

        gameSettingRepository.findByGameModeAndIsActiveTrue(request.getGameMode())
                .orElseThrow(() -> new NotFoundException("해당 게임 모드의 설정을 찾을 수 없습니다."));

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
