package com.memory.service.game;

import com.memory.domain.game.*;
import com.memory.domain.game.repository.GameSessionRepository;
import com.memory.domain.game.repository.GameSettingRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.game.GameSessionRequest;
import com.memory.dto.game.response.GameSessionResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import com.memory.service.game.factory.GameSessionFactory;
import com.memory.service.game.factory.GameSessionFactoryService;
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
    private final MemberRepository memberRepository;
    private final GameSessionFactory gameSessionFactory;

    @Transactional
    public GameSessionResponse createGameSession(Long memberId, GameSessionRequest.Create request) {
        GameSessionFactoryService gameSessionService = gameSessionFactory.getGameSessionService(request.getGameMode());
        GameSession gameSession = gameSessionService.createGameSession(memberId, request);

        GameSession savedSession = gameSessionRepository.save(gameSession);

        return GameSessionResponse.from(savedSession);
    }

    @Transactional(readOnly = true)
    public GameSessionResponse findGameSessionById(Long memberId, Long sessionId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        GameSession gameSession = gameSessionRepository.findGameSessionById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게임 세션입니다."));

        if (!gameSession.getMember().getId().equals(memberId)) {
            throw new ValidationException("본인의 게임 세션만 조회할 수 있습니다.");
        }

        return GameSessionResponse.from(gameSession);
    }

    @Transactional(readOnly = true)
    public GameSessionResponse findProgressGameSession(Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        GameSession currentSession = gameSessionRepository.findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("현재 진행중인 게임이 없습니다."));

        return GameSessionResponse.from(currentSession);
    }

    public List<GameSessionResponse> findGameSessionsByMember(Long memberId, GameSessionRequest.GetList request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        List<GameSession> gameSessions = gameSessionRepository.findByMemberAndGameMode(member, request.getGameMode());

        return gameSessions.stream()
                .map(GameSessionResponse::from)
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
}
