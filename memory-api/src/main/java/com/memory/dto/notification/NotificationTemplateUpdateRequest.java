package com.memory.dto.notification;

import jakarta.validation.constraints.Size;

/**
 * 알림 템플릿 업데이트 요청 DTO
 */
public record NotificationTemplateUpdateRequest(
        @Size(max = 255, message = "제목 템플릿은 255자 이하여야 합니다")
        String titleTemplate,
        
        @Size(max = 1000, message = "메시지 템플릿은 1000자 이하여야 합니다")
        String messageTemplate,
        
        String payloadTemplate,
        
        @Size(max = 500, message = "설명은 500자 이하여야 합니다")
        String description,
        
        Boolean isActive
) {

}
