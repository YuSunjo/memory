package com.memory.service.routine

import com.memory.domain.member.repository.MemberRepository
import com.memory.domain.routine.Routine
import com.memory.domain.routine.repository.RoutineRepository
import com.memory.domain.todo.Todo
import com.memory.domain.todo.Todo.Companion.createFromRoutine
import com.memory.domain.todo.repository.TodoRepository
import com.memory.dto.routine.RoutineRequest
import com.memory.dto.routine.response.RoutinePreviewResponse
import com.memory.dto.routine.response.RoutineResponse
import com.memory.dto.routine.response.RoutineResponse.Companion.from
import com.memory.exception.customException.NotFoundException
import com.memory.exception.customException.ValidationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.stream.Collectors

@Service
class RoutineService(
    private val memberRepository: MemberRepository,
    private val routineRepository: RoutineRepository,
    private val todoRepository: TodoRepository,
) {
    @Transactional
    fun createRoutine(memberId: Long?, request: RoutineRequest.Create): RoutineResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val routine = request.toEntity(member)
        val savedRoutine = routineRepository.save<Routine>(routine)

        return from(savedRoutine)
    }

    @Transactional
    fun updateRoutine(memberId: Long?, routineId: Long?, request: RoutineRequest.Update): RoutineResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val routine = routineRepository.findByIdAndMember(routineId, member)
            .orElseThrow { NotFoundException("루틴을 찾을 수 없습니다.") }

        routine.update(
            request.title,
            request.content,
            request.toRepeatSetting()
        )

        return from(routine)
    }

    @Transactional
    fun deleteRoutine(memberId: Long?, routineId: Long?) {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val routine = routineRepository.findByIdAndMember(routineId, member)
            .orElseThrow { NotFoundException("루틴을 찾을 수 없습니다.") }

        routine.updateDelete()
    }

    @Transactional
    fun toggleRoutineActive(memberId: Long?, routineId: Long?) {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val routine = routineRepository.findByIdAndMember(routineId, member)
            .orElseThrow { NotFoundException("루틴을 찾을 수 없습니다.") }

        if (routine.active) {
            routine.deactivate()
        } else {
            routine.activate()
        }
    }

    @Transactional(readOnly = true)
    fun getRoutines(memberId: Long?): MutableList<RoutineResponse?> {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val routines: List<Routine> = routineRepository.findAllRoutinesByMember(member)
        return routines.stream()
            .map<RoutineResponse?> { obj: Routine -> from(obj) }
            .collect(Collectors.toList())
    }

    // 특정 날짜 범위에 대한 루틴 미리보기 조회
    @Transactional(readOnly = true)
    fun getRoutineForDateRange(
        memberId: Long?,
        startDate: LocalDate,
        endDate: LocalDate
    ): MutableList<RoutinePreviewResponse?> {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val activeRoutines: List<Routine> = routineRepository.findActiveRoutinesByMember(member)

        return activeRoutines.stream()
            .flatMap { routine: Routine ->
                startDate.datesUntil(endDate.plusDays(1))
                    .filter { date: LocalDate -> routine.isApplicableOn(date.atStartOfDay()) }
                    .map<RoutinePreviewResponse?> { date: LocalDate? -> RoutinePreviewResponse.from(routine, date) }
            }
            .collect(Collectors.toList())
    }

    // 루틴을 실제 Todo로 변환
    @Transactional
    fun convertRoutineToTodo(memberId: Long?, routineId: Long?, targetDate: LocalDate) {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val routine = routineRepository.findByIdAndMember(routineId, member)
            .orElseThrow { NotFoundException("루틴을 찾을 수 없습니다.") }

        if (!routine.active) {
            throw ValidationException("비활성화된 루틴입니다.")
        }

        if (!routine.isApplicableOn(targetDate.atStartOfDay())) {
            throw ValidationException("해당 날짜에 적용할 수 없는 루틴입니다.")
        }

        // 이미 생성된 Todo가 있는지 확인
        val targetDateTime = routine.getDateTimeFor(targetDate.atStartOfDay())
        val todoExists = todoRepository.existsByMemberAndRoutineAndDueDate(member, routine, targetDateTime)

        if (todoExists) {
            throw ValidationException("이미 해당 날짜에 생성된 Todo가 있습니다.")
        }

        val todo = createFromRoutine(routine, targetDateTime, member)
        todoRepository.save<Todo?>(todo)
    }
}
