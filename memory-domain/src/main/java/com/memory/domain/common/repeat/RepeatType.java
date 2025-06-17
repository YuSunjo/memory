package com.memory.domain.common.repeat;

import lombok.Getter;

@Getter
public enum RepeatType {
    NONE("반복 없음"),
    DAILY("매일"),
    WEEKLY("매주"),
    MONTHLY("매월"),
    YEARLY("매년");
    
    private final String description;
    
    RepeatType(String description) {
        this.description = description;
    }
}