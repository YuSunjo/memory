package com.memory.service.game.factory;

import com.memory.domain.game.GameMode;
import com.memory.exception.customException.ValidationException;
import com.memory.service.game.MyMemoriesGameService;
import com.memory.service.game.MemoriesRandomGameService;
import com.memory.service.game.RandomGameService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GameFactory {

    private final Map<GameMode, GameFactoryService> gameServiceMap = new HashMap<>();

    private final MyMemoriesGameService myMemoriesGameService;
    private final MemoriesRandomGameService memoriesRandomGameService;
    private final RandomGameService randomGameService;

    @PostConstruct
    public void init() {
        gameServiceMap.put(GameMode.MY_MEMORIES, myMemoriesGameService);
        gameServiceMap.put(GameMode.RANDOM, randomGameService);
        gameServiceMap.put(GameMode.MEMORIES_RANDOM, memoriesRandomGameService);
    }

    public GameFactoryService getGameService(GameMode gameMode) {
        GameFactoryService service = gameServiceMap.get(gameMode);
        if (service == null) {
            throw new ValidationException("지원하지 않는 게임 모드입니다: " + gameMode);
        }
        return service;
    }
}
