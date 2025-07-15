-- ========================================
-- V8: Create Notification System Tables
-- ========================================

-- 1. FCM 토큰 테이블
CREATE TABLE fcm_token (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    token_value VARCHAR(1000) NOT NULL,
    device_id VARCHAR(255) NOT NULL,
    device_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL '60 days'),
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 2. 알림 템플릿 테이블
CREATE TABLE notification_template (
    id BIGSERIAL PRIMARY KEY,
    notification_type VARCHAR(50) NOT NULL,
    template_name VARCHAR(100) NOT NULL,
    title_template VARCHAR(255) NOT NULL,
    message_template VARCHAR(1000) NOT NULL,
    payload_template TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(500),
    version INTEGER NOT NULL DEFAULT 1,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP
);

-- 3. 알림 테이블
CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    sender_id BIGINT,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    resource_type VARCHAR(50),
    resource_id BIGINT,
    payload_data TEXT,
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    fcm_message_id VARCHAR(255),
    failure_reason VARCHAR(500),
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retry_count INTEGER NOT NULL DEFAULT 3,
    expires_at TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL '24 hours'),
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (sender_id) REFERENCES member(id)
);

-- ========================================
-- 인덱스 생성
-- ========================================

-- FCM 토큰 인덱스
CREATE INDEX idx_fcm_token_member ON fcm_token(member_id);
CREATE INDEX idx_fcm_token_device ON fcm_token(device_id);
CREATE INDEX idx_fcm_token_active ON fcm_token(is_active);

-- 알림 템플릿 인덱스
CREATE INDEX idx_template_type ON notification_template(notification_type);
CREATE INDEX idx_template_active ON notification_template(is_active);

-- 알림 인덱스
CREATE INDEX idx_notification_member ON notification(member_id);
CREATE INDEX idx_notification_type ON notification(notification_type);
CREATE INDEX idx_notification_status ON notification(status);

-- ========================================
-- 기본 알림 템플릿 데이터 삽입
-- ========================================

-- 메모리 관련 템플릿
INSERT INTO notification_template (notification_type, template_name, title_template, message_template, payload_template, description) VALUES
('MEMORY_CREATED', 'memory_created_default', '새로운 메모리가 생성되었습니다', '{senderName}님이 새로운 메모리 "{memoryTitle}"을 생성했습니다.', '{"action": "memory_detail", "memoryId": "{memoryId}"}', '메모리 생성 기본 템플릿'),
('MEMORY_SHARED', 'memory_shared_default', '메모리가 공유되었습니다', '{senderName}님이 "{memoryTitle}" 메모리를 공유했습니다.', '{"action": "memory_detail", "memoryId": "{memoryId}"}', '메모리 공유 기본 템플릿'),
('MEMORY_LIKED', 'memory_liked_default', '메모리에 좋아요가 눌렸습니다', '{senderName}님이 "{memoryTitle}" 메모리에 좋아요를 눌렀습니다.', '{"action": "memory_detail", "memoryId": "{memoryId}"}', '메모리 좋아요 기본 템플릿'),
('MEMORY_COMMENTED', 'memory_commented_default', '메모리에 댓글이 달렸습니다', '{senderName}님이 "{memoryTitle}" 메모리에 댓글을 남겼습니다.', '{"action": "memory_detail", "memoryId": "{memoryId}"}', '메모리 댓글 기본 템플릿');

-- 관계 관련 템플릿
INSERT INTO notification_template (notification_type, template_name, title_template, message_template, payload_template, description) VALUES
('RELATIONSHIP_REQUEST', 'relationship_request_default', '관계 요청이 도착했습니다', '{senderName}님이 관계 요청을 보냈습니다.', '{"action": "relationship_request", "relationshipId": "{relationshipId}"}', '관계 요청 기본 템플릿'),
('RELATIONSHIP_ACCEPTED', 'relationship_accepted_default', '관계 요청이 수락되었습니다', '{senderName}님이 관계 요청을 수락했습니다.', '{"action": "relationship_detail", "relationshipId": "{relationshipId}"}', '관계 수락 기본 템플릿'),
('RELATIONSHIP_REJECTED', 'relationship_rejected_default', '관계 요청이 거절되었습니다', '{senderName}님이 관계 요청을 거절했습니다.', '{"action": "relationship_list"}', '관계 거절 기본 템플릿');

-- 일정 관련 템플릿
INSERT INTO notification_template (notification_type, template_name, title_template, message_template, payload_template, description) VALUES
('CALENDAR_REMINDER', 'calendar_reminder_default', '일정 알림', '{eventTitle} 일정이 {reminderTime}에 예정되어 있습니다.', '{"action": "calendar_detail", "eventId": "{eventId}"}', '일정 알림 기본 템플릿'),
('CALENDAR_INVITATION', 'calendar_invitation_default', '일정 초대가 도착했습니다', '{senderName}님이 "{eventTitle}" 일정에 초대했습니다.', '{"action": "calendar_invitation", "eventId": "{eventId}"}', '일정 초대 기본 템플릿');

-- To-Do 관련 템플릿
INSERT INTO notification_template (notification_type, template_name, title_template, message_template, payload_template, description) VALUES
('TODO_DEADLINE', 'todo_deadline_default', '할 일 마감일 알림', '"{todoTitle}" 할 일의 마감일이 다가왔습니다.', '{"action": "todo_detail", "todoId": "{todoId}"}', 'To-Do 마감일 기본 템플릿'),
('TODO_COMPLETED', 'todo_completed_default', '할 일이 완료되었습니다', '"{todoTitle}" 할 일이 완료되었습니다.', '{"action": "todo_list"}', 'To-Do 완료 기본 템플릿');

-- 시스템 관련 템플릿
INSERT INTO notification_template (notification_type, template_name, title_template, message_template, payload_template, description) VALUES
('SYSTEM_NOTICE', 'system_notice_default', '시스템 공지사항', '{noticeTitle}', '{"action": "notice_detail", "noticeId": "{noticeId}"}', '시스템 공지 기본 템플릿'),
('SYSTEM_UPDATE', 'system_update_default', '시스템 업데이트 알림', '새로운 기능이 추가되었습니다. {updateContent}', '{"action": "app_update"}', '시스템 업데이트 기본 템플릿'),
('SYSTEM_MAINTENANCE', 'system_maintenance_default', '시스템 점검 안내', '시스템 점검이 예정되어 있습니다. {maintenanceTime}', '{"action": "maintenance_info"}', '시스템 점검 기본 템플릿');

-- ========================================
-- 테이블 코멘트 추가
-- ========================================

COMMENT ON TABLE fcm_token IS 'Firebase Cloud Messaging 토큰 관리 테이블';
COMMENT ON TABLE notification_template IS '알림 템플릿 관리 테이블';
COMMENT ON TABLE notification IS '푸시 알림 정보 관리 테이블';

-- ========================================
-- 컬럼 코멘트 추가
-- ========================================

-- FCM 토큰 테이블 코멘트
COMMENT ON COLUMN fcm_token.token_value IS 'Firebase FCM 토큰 값';
COMMENT ON COLUMN fcm_token.device_id IS '디바이스 고유 식별자';
COMMENT ON COLUMN fcm_token.device_type IS '디바이스 타입 (ANDROID, IOS, WEB)';
COMMENT ON COLUMN fcm_token.is_active IS '토큰 활성화 상태';
COMMENT ON COLUMN fcm_token.last_used_at IS '토큰 마지막 사용 시간';
COMMENT ON COLUMN fcm_token.expires_at IS '토큰 만료 시간 (기본 60일)';

-- 알림 템플릿 테이블 코멘트
COMMENT ON COLUMN notification_template.notification_type IS '알림 타입';
COMMENT ON COLUMN notification_template.template_name IS '템플릿 이름';
COMMENT ON COLUMN notification_template.title_template IS '제목 템플릿 (변수 치환 가능)';
COMMENT ON COLUMN notification_template.message_template IS '메시지 템플릿 (변수 치환 가능)';
COMMENT ON COLUMN notification_template.payload_template IS 'FCM 페이로드 템플릿 (JSON 형태)';
COMMENT ON COLUMN notification_template.version IS '템플릿 버전';

-- 알림 테이블 코멘트
COMMENT ON COLUMN notification.member_id IS '알림 수신자 ID';
COMMENT ON COLUMN notification.sender_id IS '알림 발송자 ID (시스템 알림의 경우 NULL)';
COMMENT ON COLUMN notification.notification_type IS '알림 타입';
COMMENT ON COLUMN notification.status IS '알림 처리 상태';
COMMENT ON COLUMN notification.resource_type IS '연관된 리소스 타입';
COMMENT ON COLUMN notification.resource_id IS '연관된 리소스 ID';
COMMENT ON COLUMN notification.payload_data IS 'FCM 페이로드 데이터 (JSON 형태)';
COMMENT ON COLUMN notification.scheduled_at IS '예약 전송 시간';
COMMENT ON COLUMN notification.sent_at IS '실제 전송 시간';
COMMENT ON COLUMN notification.read_at IS '읽은 시간';
COMMENT ON COLUMN notification.fcm_message_id IS 'Firebase 메시지 ID';
COMMENT ON COLUMN notification.failure_reason IS '전송 실패 사유';
COMMENT ON COLUMN notification.retry_count IS '재시도 횟수';
COMMENT ON COLUMN notification.max_retry_count IS '최대 재시도 횟수';
COMMENT ON COLUMN notification.expires_at IS '알림 만료 시간 (기본 24시간)';
