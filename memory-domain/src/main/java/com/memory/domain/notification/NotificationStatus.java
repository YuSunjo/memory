package com.memory.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 알림의 처리 상태를 나타내는 enum
 */
@Getter
@RequiredArgsConstructor
public enum NotificationStatus {
    
    CREATED("생성됨"),
    
    PENDING("전송 대기"),

    SENDING("전송 중"),

    SENT("전송 완료"),
    
    FAILED("전송 실패"),
    
    READ("읽음"),
    
    EXPIRED("만료됨");
    
    private final String description;
    
    public boolean isActive() {
        return this != EXPIRED && this != FAILED;
    }
    
    public boolean isUnread() {
        return this == SENT && this != READ;
    }
    
    public boolean isRetryable() {
        return this == FAILED || this == CREATED || this == PENDING;
    }
}
