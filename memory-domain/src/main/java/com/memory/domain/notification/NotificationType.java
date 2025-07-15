package com.memory.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 알림 타입을 정의하는 enum
 * Firebase 푸시 알림과 연동되는 알림 유형들을 관리
 */
@Getter
@RequiredArgsConstructor
public enum NotificationType {
    
    // 메모리 관련 알림
    MEMORY_CREATED("새로운 메모리가 생성되었습니다", "memory"),
    MEMORY_SHARED("메모리가 공유되었습니다", "memory"),
    MEMORY_LIKED("메모리에 좋아요가 눌렸습니다", "memory"),
    MEMORY_COMMENTED("메모리에 댓글이 달렸습니다", "memory"),
    
    // 관계 관련 알림
    RELATIONSHIP_REQUEST("관계 요청이 도착했습니다", "relationship"),
    RELATIONSHIP_ACCEPTED("관계 요청이 수락되었습니다", "relationship"),
    RELATIONSHIP_REJECTED("관계 요청이 거절되었습니다", "relationship"),

    // 게임 관련 알림
    GAME_INVITATION("게임 초대가 도착했습니다", "game"),
    GAME_STARTED("게임이 시작되었습니다", "game"),
    GAME_RESULT("게임 결과가 나왔습니다", "game"),

    // 일정 관련 알림
    CALENDAR_REMINDER("일정 알림입니다", "calendar"),
    CALENDAR_INVITATION("일정 초대가 도착했습니다", "calendar"),
    
    // To-Do 관련 알림
    TODO_DEADLINE("할 일 마감일이 다가왔습니다", "todo"),
    TODO_COMPLETED("할 일이 완료되었습니다", "todo"),
    
    // 시스템 알림
    SYSTEM_NOTICE("시스템 공지사항입니다", "system"),
    SYSTEM_UPDATE("시스템 업데이트 알림입니다", "system"),
    SYSTEM_MAINTENANCE("시스템 점검 안내입니다", "system");
    
    private final String defaultMessage;
    private final String category;
    
    public String getFirebaseType() {
        return this.name().toLowerCase();
    }
    
    public boolean isImmediateDelivery() {
        return switch (this) {
            case MEMORY_CREATED, MEMORY_SHARED, RELATIONSHIP_REQUEST, 
                 GAME_INVITATION, CALENDAR_REMINDER -> true;
            default -> false;
        };
    }
    
    public boolean isBatchable() {
        return switch (this) {
            case MEMORY_LIKED, MEMORY_COMMENTED, TODO_DEADLINE, 
                 SYSTEM_NOTICE -> true;
            default -> false;
        };
    }
}
