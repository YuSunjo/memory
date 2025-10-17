package com.memory.service.game;

import com.memory.domain.game.GameQuestion;
import com.memory.domain.game.GameSession;
import com.memory.domain.game.GameSetting;
import com.memory.domain.game.repository.GameQuestionRepository;
import com.memory.domain.game.repository.GameSessionRepository;
import com.memory.domain.game.repository.GameSettingRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.game.GameQuestionRequest;
import com.memory.dto.game.response.GameQuestionResponse;
import com.memory.exception.customException.ConflictException;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import com.memory.service.game.factory.GameFactory;
import com.memory.service.game.factory.GameFactoryService;
import com.memory.util.GeographyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

        Integer nextOrder = validateQuestionGeneration(gameSession, gameSetting);

        // 게임 모드에 따라 적절한 GameFactoryService
        GameFactoryService gameService = gameFactory.getGameService(gameSession.getGameMode());
        GameQuestionResponse nextQuestion = gameService.getNextQuestion(member, gameSession, gameSetting, nextOrder);

        log.info("다음 문제 생성 완료 - memberId: {}, sessionId: {}, nextOrder: {}",
                member.getId(), gameSession.getId(), nextOrder);
        return nextQuestion;
    }

    @Transactional
    public GameQuestionResponse submitAnswer(Long memberId, Long sessionId, Long questionId, 
                                           GameQuestionRequest.SubmitAnswer request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        GameSession gameSession = gameSessionRepository.findGameSessionById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게임 세션입니다."));

        if (!gameSession.isOwner(member)) {
            throw new ConflictException("본인의 게임 세션만 답안 제출이 가능합니다.");
        }
        if (!gameSession.isInProgress()) {
            throw new ValidationException("진행 중인 게임 세션이 아닙니다.");
        }

        GameQuestion gameQuestion = gameQuestionRepository.findByIdAndGameSession(questionId, gameSession)
                .orElseThrow(() -> new NotFoundException("해당 게임 세션에서 문제를 찾을 수 없습니다."));

        if (gameQuestion.isAnswered()) {
            throw new ConflictException("이미 답안이 제출된 문제입니다.");
        }

        GameSetting gameSetting = gameSettingRepository.findByGameModeAndIsActiveTrue(gameSession.getGameMode())
                .orElseThrow(() -> new NotFoundException("해당 게임 모드의 설정을 찾을 수 없습니다."));

        // 거리 계산
        BigDecimal distanceKm = GeographyUtils.calculateDistance(
                gameQuestion.getCorrectLatitude(),
                gameQuestion.getCorrectLongitude(),
                request.getPlayerLatitude(),
                request.getPlayerLongitude()
        );

        // 점수 계산
        Integer score = GeographyUtils.calculateScore(distanceKm, gameSetting.getMaxDistanceForFullScoreKm());

        // 답안 제출
        gameQuestion.submitAnswer(
                request.getPlayerLatitude(),
                request.getPlayerLongitude(),
                distanceKm,
                score,
                request.getTimeTakenSeconds()
        );

        // 게임 세션 점수 업데이트
        gameSession.updateScore(score);
        
        // 정답 여부 확인 및 정답 수 증가
        if (gameQuestion.isCorrectAnswer(gameSetting.getMaxDistanceForFullScoreKm())) {
            gameSession.incrementCorrectAnswers();
        }

        boolean isGameSessionCompleted = false;
        // 모든 문제 완료 시 게임 완료 처리
        if (gameSession.isAnsweredAllQuestions(gameSetting.getMaxQuestions())) {
            gameSession.completeGame();
            isGameSessionCompleted = true;
        }

        log.info("답안 제출 완료 - memberId: {}, sessionId: {}, questionId: {}, score: {}, distance: {}km",
                member.getId(), gameSession.getId(), questionId, score, distanceKm);

        return GameQuestionResponse.forAnsweredQuestion(gameQuestion, null, isGameSessionCompleted);
    }

    private Integer validateQuestionGeneration(GameSession gameSession, GameSetting gameSetting) {
        int nextOrder = gameQuestionRepository.findNextQuestionOrder(gameSession) - 1;

        if (nextOrder > gameSetting.getMaxQuestions()) {
            throw new ConflictException("모든 문제가 완료되었습니다.");
        }
        return nextOrder;
    }
}
