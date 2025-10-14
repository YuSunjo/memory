package com.memory.domain.notification.repository

import com.memory.domain.notification.Notification
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long>, NotificationRepositoryCustom
