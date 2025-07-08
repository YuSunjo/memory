package com.memory.service.game;

import com.memory.domain.game.GameMode;
import com.memory.domain.game.GameSession;
import com.memory.domain.game.GameSessionStatus;
import com.memory.domain.game.repository.GameSessionRepository;
import com.memory.domain.game.repository.GameSettingRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.memory.Memory;
import com.memory.domain.memory.repository.MemoryRepository;
import com.memory.dto.game.GameSessionRequest;
import com.memory.exception.customException.ConflictException;
import com.memory.exception.customException.NotFoundException;
import com.memory.service.game.factory.GameSessionFactoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.memory.domain.game.GameSession.gameSessionInit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyMemoriesGameService implements GameSessionFactoryService {

    private final MemberRepository memberRepository;
    private final GameSessionRepository gameSessionRepository;
    private final GameSettingRepository gameSettingRepository;
    private final MemoryRepository memoryRepository;
    
    private static final int MIN_MEMORIES_FOR_GAME = 3;

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

        List<Memory> availableMemories = memoryRepository.findMemoriesWithImagesByMember(member);
        if (availableMemories.size() < MIN_MEMORIES_FOR_GAME) {
            throw new IllegalStateException(
                    String.format("내 추억 게임을 시작하기 위해서는 최소 %d개의 이미지가 있는 추억이 필요합니다.", MIN_MEMORIES_FOR_GAME)
            );
        }
        
        GameSession gameSession = gameSessionInit(member, GameMode.MY_MEMORIES);
        
        log.info("내 추억 게임 세션 생성 완료 - memberId: {}, sessionId: {}", 
                member.getId(), gameSession.getId());
        
        return gameSession;
    }

}
