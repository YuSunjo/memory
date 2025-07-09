package com.memory.service.game;

import com.memory.domain.game.GameSession;
import com.memory.domain.game.GameSetting;
import com.memory.domain.game.repository.GameQuestionRepository;
import com.memory.domain.game.repository.GameSessionRepository;
import com.memory.domain.game.repository.GameSettingRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.game.response.GameQuestionResponse;
import com.memory.exception.customException.ConflictException;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import com.memory.service.game.factory.GameFactory;
import com.memory.service.game.factory.GameFactoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameQuestionService {

    private final GameSessionRepository gameSessionRepository;
    private final GameSettingRepository gameSettingRepository;
    private final GameQuestionRepository gameQuestionRepository;
    private final MemberRepository memberRepository;
    private final GameFactory gameFactory;

    @Transactional
    public GameQuestionResponse getNextQuestion(Long memberId, Long sessionId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        GameSession gameSession = gameSessionRepository.findGameSessionById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게임 세션입니다."));

        if (!gameSession.isOwner(member)) {
            throw new ConflictException("본인의 게임 세션만 조회할 수 있습니다.");
        }
        if (!gameSession.isInProgress()) {
            throw new ValidationException("진행 중인 게임 세션이 아닙니다.");
        }

        GameSetting gameSetting = gameSettingRepository.findByGameModeAndIsActiveTrue(gameSession.getGameMode())
                .orElseThrow(() -> new NotFoundException("해당 게임 모드의 설정을 찾을 수 없습니다."));

        // 검증
        Integer nextOrder = validateQuestionGeneration(gameSession, gameSetting);

        GameFactoryService gameService = gameFactory.getGameService(gameSession.getGameMode());
        GameQuestionResponse nextQuestion = gameService.getNextQuestion(member, gameSession, gameSetting, nextOrder);

        log.info("다음 문제 생성 완료 - memberId: {}, sessionId: {}, nextOrder: {}",
                member.getId(), gameSession.getId(), nextOrder);
        return nextQuestion;
    }

    private Integer validateQuestionGeneration(GameSession gameSession, GameSetting gameSetting) {
        int nextOrder = gameQuestionRepository.findNextQuestionOrder(gameSession) - 1;

        if (nextOrder > gameSetting.getMaxQuestions()) {
            throw new ConflictException("모든 문제가 완료되었습니다.");
        }
        return nextOrder;
    }
}
