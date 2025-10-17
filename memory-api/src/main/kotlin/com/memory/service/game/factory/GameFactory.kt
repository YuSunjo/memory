package com.memory.service.game.factory

import com.memory.domain.game.GameMode
import com.memory.exception.customException.ValidationException
import com.memory.service.game.MemoriesRandomGameService
import com.memory.service.game.MyMemoriesGameService
import com.memory.service.game.RandomGameService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class GameFactory(
    private val myMemoriesGameService: MyMemoriesGameService,
    private val memoriesRandomGameService: MemoriesRandomGameService,
    private val randomGameService: RandomGameService
) {
    private val gameServiceMap: MutableMap<GameMode, GameFactoryService> = HashMap()

    @PostConstruct
    fun init() {
        gameServiceMap[GameMode.MY_MEMORIES] = myMemoriesGameService
        gameServiceMap[GameMode.RANDOM] = randomGameService
        gameServiceMap[GameMode.MEMORIES_RANDOM] = memoriesRandomGameService
    }

    fun getGameService(gameMode: GameMode): GameFactoryService {
        return gameServiceMap[gameMode]
            ?: throw ValidationException("지원하지 않는 게임 모드입니다: $gameMode")
    }
}
