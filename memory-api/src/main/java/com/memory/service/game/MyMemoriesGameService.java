package com.memory.service.game;

import com.memory.domain.file.File;
import com.memory.domain.game.*;
import com.memory.domain.game.repository.GameQuestionRepository;
import com.memory.domain.member.Member;
import com.memory.domain.memory.Memory;
import com.memory.domain.memory.repository.MemoryRepository;
import com.memory.dto.game.GameSessionRequest;
import com.memory.dto.game.response.GameQuestionResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.service.game.factory.GameFactoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.memory.domain.game.GameSession.gameSessionInit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyMemoriesGameService implements GameFactoryService {

    private final GameQuestionRepository gameQuestionRepository;
    private final MemoryRepository memoryRepository;
    
    private static final int MIN_MEMORIES_FOR_GAME = 3;

    @Override
    public GameSession createGameSession(Member member, GameSetting gameSetting, GameSessionRequest.Create request) {
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

    @Override
    public GameQuestionResponse getNextQuestion(Member member, GameSession gameSession, GameSetting gameSetting, Integer nextOrder) {
        // 이미 사용된 Memory ID들 조회
        List<GameQuestion> existingQuestions = gameQuestionRepository.findByGameSessionOrderByQuestionOrder(gameSession);
        List<Long> usedMemoryIds = existingQuestions.stream()
                .map(q -> q.getMemory().getId())
                .collect(Collectors.toList());

        Memory selectedMemory = selectRandomMemory(gameSession, usedMemoryIds);

        // GameQuestion 생성
        String latitude = selectedMemory.getMap().getLatitude();
        String longitude = selectedMemory.getMap().getLongitude();

        GameQuestion gameQuestion = GameQuestion.init(
                gameSession,
                selectedMemory,
                nextOrder,
                new BigDecimal(latitude),
                new BigDecimal(longitude),
                selectedMemory.getLocationName()
        );

        GameQuestion savedQuestion = gameQuestionRepository.save(gameQuestion);

        gameSession.addGameQuestion(savedQuestion);

        List<String> imageUrls = selectedMemory.getFiles().stream()
                .map(File::getFileUrl)
                .toList();

        return GameQuestionResponse.forQuestion(savedQuestion, imageUrls);
    }

    private Memory selectRandomMemory(GameSession gameSession, List<Long> usedMemoryIds) {
        List<Memory> availableMemories = memoryRepository.findMemoriesWithImagesByMember(gameSession.getMember());

        // 이미 사용된 Memory 제외
        List<Memory> unusedMemories = availableMemories.stream()
                .filter(memory -> !usedMemoryIds.contains(memory.getId()))
                .collect(Collectors.toList());

        if (unusedMemories.isEmpty()) {
            throw new NotFoundException("사용 가능한 내 추억이 부족합니다.");
        }

        Collections.shuffle(unusedMemories);
        return unusedMemories.get(0);
    }

}
