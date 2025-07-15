package com.memory.dto.notification;

import com.memory.domain.notification.Notification;
import com.memory.domain.notification.NotificationStatus;
import com.memory.domain.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long memberId,
        String memberName,
        Long senderId,
        String senderName,
        NotificationType notificationType,
        String title,
        String message,
        NotificationStatus status,
        String resourceType,
        Long resourceId,
        String payloadData,
        LocalDateTime scheduledAt,
        LocalDateTime sentAt,
        LocalDateTime readAt,
        String fcmMessageId,
        String failureReason,
        Integer retryCount,
        LocalDateTime expiresAt,
        LocalDateTime createDate,
        LocalDateTime updateDate
) {
    
    /**
     * Entity -> DTO 변환
     */
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getMember().getId(),
                notification.getMember().getName(),
                notification.getSender() != null ? notification.getSender().getId() : null,
                notification.getSender() != null ? notification.getSender().getName() : null,
                notification.getNotificationType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getResourceType(),
                notification.getResourceId(),
                notification.getPayloadData(),
                notification.getScheduledAt(),
                notification.getSentAt(),
                notification.getReadAt(),
                notification.getFcmMessageId(),
                notification.getFailureReason(),
                notification.getRetryCount(),
                notification.getExpiresAt(),
                notification.getCreateDate(),
                notification.getUpdateDate()
        );
    }
    
    /**
     * 간단한 알림 정보만 포함하는 요약 DTO
     */
    public record Summary(
            Long id,
            NotificationType notificationType,
            String title,
            String message,
            NotificationStatus status,
            boolean isRead,
            LocalDateTime createDate
    ) {
        public static Summary from(Notification notification) {
            return new Summary(
                    notification.getId(),
                    notification.getNotificationType(),
                    notification.getTitle(),
                    notification.getMessage(),
                    notification.getStatus(),
                    notification.getStatus() == NotificationStatus.READ,
                    notification.getCreateDate()
            );
        }
    }
    
    /**
     * 알림 리스트용 간소화된 DTO
     */
    public record ListItem(
            Long id,
            String title,
            String message,
            NotificationType notificationType,
            NotificationStatus status,
            String senderName,
            LocalDateTime createDate,
            boolean isExpired
    ) {
        public static ListItem from(Notification notification) {
            return new ListItem(
                    notification.getId(),
                    notification.getTitle(),
                    notification.getMessage(),
                    notification.getNotificationType(),
                    notification.getStatus(),
                    notification.getSender() != null ? notification.getSender().getName() : "시스템",
                    notification.getCreateDate(),
                    notification.isExpired()
            );
        }
    }
}
