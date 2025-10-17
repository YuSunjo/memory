package com.memory.service.game.factory

import com.memory.domain.game.GameMode
import com.memory.exception.customException.ValidationException
import com.memory.service.game.MemoriesRandomGameService
import com.memory.service.game.MyMemoriesGameService
import com.memory.service.game.RandomGameService
import jakarta.annotation.PostConstruct
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
class GameFactory {
    private val gameServiceMap: MutableMap<GameMode?, GameFactoryService> = HashMap<GameMode?, GameFactoryService>()

    private val myMemoriesGameService: MyMemoriesGameService? = null
    private val memoriesRandomGameService: MemoriesRandomGameService? = null
    private val randomGameService: RandomGameService? = null

    @PostConstruct
    fun init() {
        gameServiceMap.put(GameMode.MY_MEMORIES, myMemoriesGameService!!)
        gameServiceMap.put(GameMode.RANDOM, randomGameService!!)
        gameServiceMap.put(GameMode.MEMORIES_RANDOM, memoriesRandomGameService!!)
    }

    fun getGameService(gameMode: GameMode?): GameFactoryService {
        val service: GameFactoryService = gameServiceMap[gameMode]
            ?: throw ValidationException("지원하지 않는 게임 모드입니다: $gameMode")
        return service
    }
}
