package com.memory.domain.notification.repository

import com.memory.domain.notification.NotificationTemplate
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationTemplateRepository : JpaRepository<NotificationTemplate, Long>, NotificationTemplateRepositoryCustom
