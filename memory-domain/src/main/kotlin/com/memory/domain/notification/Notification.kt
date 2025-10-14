package com.memory.domain.notification

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 알림 정보를 저장하는 Entity
 * Firebase FCM을 통해 전송되는 개별 알림을 관리
 */
@Entity
@Table(
    name = "notification",
    indexes = [
        Index(name = "idx_notification_member", columnList = "member_id"),
        Index(name = "idx_notification_type", columnList = "notification_type"),
        Index(name = "idx_notification_status", columnList = "status")
    ]
)
class Notification private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    /**
     * 알림 발송자 (시스템 알림의 경우 null 가능)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    var sender: Member? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    var notificationType: NotificationType,

    @Column(name = "title", nullable = false, length = 255)
    var title: String,

    @Column(name = "message", nullable = false, length = 1000)
    var message: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: NotificationStatus = NotificationStatus.CREATED,

    /**
     * 연관된 리소스 타입 (예: memory, calendar 등)
     */
    @Column(name = "resource_type", length = 50)
    var resourceType: String = notificationType.category,

    /**
     * 연관된 리소스 ID
     */
    @Column(name = "resource_id")
    var resourceId: Long? = null,

    /**
     * Firebase FCM에서 사용할 추가 데이터 (JSON 형태)
     */
    @Column(name = "payload_data", columnDefinition = "TEXT")
    var payloadData: String? = null,

    @Column(name = "scheduled_at")
    var scheduledAt: LocalDateTime? = null,

    @Column(name = "sent_at")
    var sentAt: LocalDateTime? = null,

    @Column(name = "read_at")
    var readAt: LocalDateTime? = null,

    /**
     * Firebase 메시지 ID (전송 후 FCM에서 반환)
     */
    @Column(name = "fcm_message_id", length = 255)
    var fcmMessageId: String? = null,

    @Column(name = "failure_reason", length = 500)
    var failureReason: String? = null,

    @Column(name = "retry_count", nullable = false)
    var retryCount: Int = 0,

    @Column(name = "max_retry_count", nullable = false)
    var maxRetryCount: Int = 3,

    @Column(name = "expires_at")
    var expiresAt: LocalDateTime = LocalDateTime.now().plusHours(24) // 기본적으로 24시간 후 만료
) : BaseTimeEntity() {

    companion object {
        fun create(
            member: Member,
            sender: Member?,
            notificationType: NotificationType,
            title: String,
            message: String
        ): Notification {
            return Notification(
                member = member,
                sender = sender,
                notificationType = notificationType,
                title = title,
                message = message
            )
        }

        fun create(
            member: Member,
            notificationType: NotificationType,
            title: String,
            message: String
        ): Notification {
            return create(member, null, notificationType, title, message)
        }
    }



    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)

}
