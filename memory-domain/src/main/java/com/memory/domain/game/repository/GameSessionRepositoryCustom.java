package com.memory.domain.game.repository;

import com.memory.domain.game.GameMode;
import com.memory.domain.game.GameSession;
import com.memory.domain.game.GameSessionStatus;
import com.memory.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface GameSessionRepositoryCustom {

    Optional<GameSession> findGameSessionById(Long sessionId);

    Optional<GameSession> findByMemberAndStatus(Member member, GameSessionStatus gameSessionStatus);

    List<GameSession> findByMemberAndGameMode(Member member, GameMode gameMode, Long lastSessionId, Integer size);

}
