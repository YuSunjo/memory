package com.memory.domain.calendar.repository

import com.memory.domain.calendar.AnniversaryEvent

interface AnniversaryEventRepository : BaseCalendarEventRepository<AnniversaryEvent>, AnniversaryEventRepositoryCustom
