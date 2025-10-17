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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final GameSettingRepository gameSettingRepository;
    private final MemberRepository memberRepository;
    private final GameFactory gameFactory;

    @Transactional
    public GameSessionResponse createGameSession(Long memberId, GameSessionRequest.Create request) {
        Member member = validateAndGetMember(memberId);
        validateNoInProgressGame(member);
        GameSetting gameSetting = validateAndGetGameSetting(request.getGameMode());

        // 게임 모드별 로직
        GameFactoryService gameService = gameFactory.getGameService(request.getGameMode());
        GameSession gameSession = gameService.createGameSession(member, gameSetting, request);

        GameSession savedSession = gameSessionRepository.save(gameSession);

        return GameSessionResponse.from(savedSession, gameSetting);
    }

    @Transactional(readOnly = true)
    public GameSessionResponse findGameSessionById(Long memberId, Long sessionId) {
        validateAndGetMember(memberId);

        GameSession gameSession = gameSessionRepository.findGameSessionById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게임 세션입니다."));

        if (!gameSession.getMember().getId().equals(memberId)) {
            throw new ValidationException("본인의 게임 세션만 조회할 수 있습니다.");
        }

        GameSetting gameSetting = gameSettingRepository.findByGameModeAndIsActiveTrue(gameSession.getGameMode())
                .orElse(null);

        return GameSessionResponse.from(gameSession, gameSetting);
    }

    @Transactional(readOnly = true)
    public GameSessionResponse findProgressGameSession(Long memberId) {
        Member member = validateAndGetMember(memberId);

        GameSession currentSession = gameSessionRepository.findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("현재 진행중인 게임이 없습니다."));

        GameSetting gameSetting = gameSettingRepository.findByGameModeAndIsActiveTrue(currentSession.getGameMode())
                .orElse(null);

        return GameSessionResponse.from(currentSession, gameSetting);
    }

    public List<GameSessionResponse> findGameSessionsByMember(Long memberId, GameSessionRequest.GetList request) {
        Member member = validateAndGetMember(memberId);

        List<GameSession> gameSessions = gameSessionRepository.findByMemberAndGameMode(member, request.getGameMode(), request.getLastSessionId(), request.getSize());

        return gameSessions.stream()
                .map(gameSession -> {
                    GameSetting gameSetting = gameSettingRepository.findByGameModeAndIsActiveTrue(gameSession.getGameMode())
                            .orElse(null);
                    return GameSessionResponse.from(gameSession, gameSetting);
                })
                .toList();
    }

    @Transactional
    public void giveUpGameSession(Long memberId, Long sessionId) {
        memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        GameSession gameSession = gameSessionRepository.findGameSessionById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게임 세션입니다."));

        if (!gameSession.getMember().getId().equals(memberId)) {
            throw new ValidationException("본인의 게임 세션만 포기할 수 있습니다.");
        }

        if (!gameSession.isInProgress()) {
            throw new ValidationException("진행중인 게임만 포기할 수 있습니다.");
        }

        gameSession.giveUpGameSession();
        log.info("게임 세션 포기 완료 - memberId: {}, sessionId: {}", memberId, sessionId);
    }

    private Member validateAndGetMember(Long memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    private void validateNoInProgressGame(Member member) {
        gameSessionRepository.findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS)
                .ifPresent(session -> {
                    throw new ConflictException("이미 진행중인 게임이 있습니다. 먼저 완료하거나 포기해주세요.");
                });
    }

    private GameSetting validateAndGetGameSetting(GameMode gameMode) {
        return gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode)
                .orElseThrow(() -> new NotFoundException("해당 게임 모드의 설정을 찾을 수 없습니다."));
    }
}
