package com.memory.domain.game

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class GameMode {
    MY_MEMORIES,        // 내 추억
    // FRIEND_MEMORIES, // 친구 추억
    MEMORIES_RANDOM,    // 랜덤 public 추억
    RANDOM;             // 메모리가 아닌 거리뷰만 보고 맞추는 게임

    @JsonValue
    fun toValue(): String = this.name

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromString(value: String?): GameMode? {
            if (value == null) return null

            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException(
                    "Invalid GameMode: $value. Valid values are: MY_MEMORIES, FRIEND_MEMORIES, RANDOM"
                )
            }
        }
    }
}
