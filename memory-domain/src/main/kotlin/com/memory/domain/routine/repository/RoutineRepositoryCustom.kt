package com.memory.domain.routine.repository

import com.memory.domain.member.Member
import com.memory.domain.routine.Routine
import java.util.Optional

interface RoutineRepositoryCustom {

    fun findActiveRoutinesByMember(member: Member?): List<Routine>

    fun findAllRoutinesByMember(member: Member?): List<Routine>

    fun findByIdAndMember(id: Long?, member: Member?): Optional<Routine>
}
