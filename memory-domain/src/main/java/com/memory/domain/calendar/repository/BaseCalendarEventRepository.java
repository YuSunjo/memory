package com.memory.domain.calendar.repository;

import com.memory.domain.calendar.BaseCalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseCalendarEventRepository<T extends BaseCalendarEvent> extends JpaRepository<T, Long> {
}