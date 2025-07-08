package com.memory.domain.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GameMode {
    MY_MEMORIES,     // 내 추억
    // FRIEND_MEMORIES, // 친구 추억
    MEMORIES_RANDOM,          // 랜덤 public 추억
    RANDOM                    // 메모리가 아닌 거리뷰만 보고 맞추는 게임
    ;

    @JsonCreator
    public static GameMode fromString(String value) {
        if (value == null) {
            return null;
        }

        try {
            return GameMode.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid GameMode: " + value +
                ". Valid values are: MY_MEMORIES, FRIEND_MEMORIES, RANDOM");
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
