package com.memory.domain.notification.repository

import com.memory.domain.notification.FcmToken
import org.springframework.data.jpa.repository.JpaRepository

interface FcmTokenRepository : JpaRepository<FcmToken, Long>, FcmTokenRepositoryCustom
