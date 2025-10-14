package com.memory.domain.calendar.repository

import com.memory.domain.calendar.BaseCalendarEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BaseCalendarEventRepository<T : BaseCalendarEvent> : JpaRepository<T, Long>
