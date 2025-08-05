package com.memory.dto.notification;

import com.memory.domain.notification.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationCreateRequest(
        
        @NotNull(message = "수신자 ID는 필수입니다")
        Long memberId,
        
        Long senderId, // null 가능 (시스템 알림의 경우)
        
        @NotNull(message = "알림 타입은 필수입니다")
        NotificationType notificationType,
        
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 255, message = "제목은 255자 이하여야 합니다")
        String title,
        
        @NotBlank(message = "메시지는 필수입니다")
        @Size(max = 1000, message = "메시지는 1000자 이하여야 합니다")
        String message,
        
        String resourceType,
        
        Long resourceId,
        
        Map<String, Object> payloadData,
        
        LocalDateTime scheduledAt, // 예약 전송 시간
        
        LocalDateTime expiresAt // 만료 시간
) {
    
    public NotificationCreateRequest {
        // 유효성 검사
        if (scheduledAt != null && scheduledAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("예약 시간은 현재 시간보다 미래여야 합니다");
        }
        
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료 시간은 현재 시간보다 미래여야 합니다");
        }
    }
    
    public static NotificationCreateRequest immediate(
            Long memberId,
            Long senderId,
            NotificationType notificationType,
            String title,
            String message) {
        return new NotificationCreateRequest(
                memberId, senderId, notificationType, title, message,
                null, null, null, null, null
        );
    }
    
    public static NotificationCreateRequest system(
            Long memberId,
            NotificationType notificationType,
            String title,
            String message) {
        return new NotificationCreateRequest(
                memberId, null, notificationType, title, message,
                "system", null, null, null, null
        );
    }
    
    /**
     * 높은 우선순위 알림 생성용 팩토리 메서드
     */
    public static NotificationCreateRequest highPriority(
            Long memberId,
            Long senderId,
            NotificationType notificationType,
            String title,
            String message,
            String resourceType,
            Long resourceId) {
        return new NotificationCreateRequest(
                memberId, senderId, notificationType, title, message,
                resourceType, resourceId, null, null, null
        );
    }
    
    /**
     * 예약 전송 알림 생성용 팩토리 메서드
     */
    public static NotificationCreateRequest scheduled(
            Long memberId,
            Long senderId,
            NotificationType notificationType,
            String title,
            String message,
            LocalDateTime scheduledAt) {
        return new NotificationCreateRequest(
                memberId, senderId, notificationType, title, message,
                null, null, null, scheduledAt, null
        );
    }
}
