package com.memory.domain.calendar.repository

import com.memory.domain.calendar.PersonalEvent

interface PersonalEventRepository : BaseCalendarEventRepository<PersonalEvent>, PersonalEventRepositoryCustom
