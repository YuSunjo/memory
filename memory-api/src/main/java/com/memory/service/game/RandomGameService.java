package com.memory.service.game;

import com.memory.domain.game.GameMode;
import com.memory.domain.game.GameSession;
import com.memory.domain.game.GameSessionStatus;
import com.memory.domain.game.repository.GameSessionRepository;
import com.memory.domain.game.repository.GameSettingRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.game.GameSessionRequest;
import com.memory.exception.customException.ConflictException;
import com.memory.exception.customException.NotFoundException;
import com.memory.service.game.factory.GameSessionFactoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.memory.domain.game.GameSession.gameSessionInit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RandomGameService implements GameSessionFactoryService {

    private final MemberRepository memberRepository;
    private final GameSessionRepository gameSessionRepository;
    private final GameSettingRepository gameSettingRepository;

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

}
