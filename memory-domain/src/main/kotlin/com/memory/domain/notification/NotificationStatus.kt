package com.memory.domain.notification

/**
 * 알림의 처리 상태를 나타내는 enum
 */
enum class NotificationStatus(val description: String) {
    CREATED("생성됨"),
    PENDING("전송 대기"),
    SENDING("전송 중"),
    SENT("전송 완료"),
    FAILED("전송 실패"),
    READ("읽음"),
    EXPIRED("만료됨");

    fun isActive(): Boolean = this != EXPIRED && this != FAILED

    fun isUnread(): Boolean = this == SENT && this != READ

    fun isRetryable(): Boolean = this == FAILED || this == CREATED || this == PENDING
}
