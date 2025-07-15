package com.memory.domain.notification;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 알림 정보를 저장하는 Entity
 * Firebase FCM을 통해 전송되는 개별 알림을 관리
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "notification",
    indexes = {
        @Index(name = "idx_notification_member", columnList = "member_id"),
        @Index(name = "idx_notification_type", columnList = "notification_type"),
        @Index(name = "idx_notification_status", columnList = "status")
    }
)
public class Notification extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    /**
     * 알림 발송자 (시스템 알림의 경우 null 가능)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "message", nullable = false, length = 1000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;
    
    /**
     * 연관된 리소스 타입 (예: memory, calendar 등)
     */
    @Column(name = "resource_type", length = 50)
    private String resourceType;
    
    /**
     * 연관된 리소스 ID
     */
    @Column(name = "resource_id")
    private Long resourceId;
    
    /**
     * Firebase FCM에서 사용할 추가 데이터 (JSON 형태)
     */
    @Column(name = "payload_data", columnDefinition = "TEXT")
    private String payloadData;
    
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    /**
     * Firebase 메시지 ID (전송 후 FCM에서 반환)
     */
    @Column(name = "fcm_message_id", length = 255)
    private String fcmMessageId;
    
    @Column(name = "failure_reason", length = 500)
    private String failureReason;
    
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;
    
    @Column(name = "max_retry_count", nullable = false)
    private Integer maxRetryCount;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    public Notification(Member member, Member sender, NotificationType notificationType,
                       String title, String message) {
        this.member = member;
        this.sender = sender;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.status = NotificationStatus.CREATED;
        this.retryCount = 0;
        this.maxRetryCount = 3;
        this.resourceType = notificationType.getCategory();
        // 기본적으로 24시간 후 만료
        this.expiresAt = LocalDateTime.now().plusHours(24);
    }
    
    public Notification(Member member, NotificationType notificationType, 
                       String title, String message) {
        this(member, null, notificationType, title, message);
    }
    
    public void setResource(String resourceType, Long resourceId) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    public void setPayloadData(String payloadData) {
        this.payloadData = payloadData;
    }
    
    public void scheduleAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
        this.status = NotificationStatus.PENDING;
    }
    
    public void startSending() {
        if (this.status != NotificationStatus.CREATED && this.status != NotificationStatus.PENDING) {
            throw new IllegalStateException("전송 가능한 상태가 아닙니다: " + this.status);
        }
        this.status = NotificationStatus.SENDING;
    }
    
    public void markAsSent(String fcmMessageId) {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.fcmMessageId = fcmMessageId;
        this.failureReason = null;
    }
    
    public void markAsFailed(String failureReason) {
        this.status = NotificationStatus.FAILED;
        this.failureReason = failureReason;
        this.retryCount++;
    }
    
    public void markAsRead() {
        if (this.status == NotificationStatus.SENT) {
            this.status = NotificationStatus.READ;
            this.readAt = LocalDateTime.now();
        }
    }
    
    public void markAsExpired() {
        this.status = NotificationStatus.EXPIRED;
    }
    
    public boolean canRetry() {
        return this.status == NotificationStatus.FAILED && 
               this.retryCount < this.maxRetryCount &&
               (this.expiresAt == null || LocalDateTime.now().isBefore(this.expiresAt));
    }
    
    public boolean isExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }
    
    public boolean isReadyToSend() {
        if (isExpired()) {
            return false;
        }
        
        if (scheduledAt != null) {
            return LocalDateTime.now().isAfter(scheduledAt) || LocalDateTime.now().isEqual(scheduledAt);
        }
        
        return status == NotificationStatus.CREATED || status == NotificationStatus.PENDING;
    }
    
}
