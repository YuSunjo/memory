package com.memory.service.routine;

import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.routine.Routine;
import com.memory.domain.routine.repository.RoutineRepository;
import com.memory.domain.todo.Todo;
import com.memory.domain.todo.repository.TodoRepository;
import com.memory.dto.routine.RoutineRequest;
import com.memory.dto.routine.response.RoutineResponse;
import com.memory.dto.routine.response.RoutinePreviewResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final MemberRepository memberRepository;
    private final RoutineRepository routineRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public RoutineResponse createRoutine(Long memberId, RoutineRequest.Create request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Routine routine = request.toEntity(member);
        Routine savedRoutine = routineRepository.save(routine);
        
        return RoutineResponse.from(savedRoutine);
    }

    @Transactional
    public RoutineResponse updateRoutine(Long memberId, Long routineId, RoutineRequest.Update request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Routine routine = routineRepository.findByIdAndMember(routineId, member)
                .orElseThrow(() -> new NotFoundException("루틴을 찾을 수 없습니다."));

        routine.update(
                request.getTitle(),
                request.getContent(),
                request.toRepeatSetting()
        );

        return RoutineResponse.from(routine);
    }

    @Transactional
    public void deleteRoutine(Long memberId, Long routineId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Routine routine = routineRepository.findByIdAndMember(routineId, member)
                .orElseThrow(() -> new NotFoundException("루틴을 찾을 수 없습니다."));

        routine.updateDelete();
    }

    @Transactional
    public void toggleRoutineActive(Long memberId, Long routineId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Routine routine = routineRepository.findByIdAndMember(routineId, member)
                .orElseThrow(() -> new NotFoundException("루틴을 찾을 수 없습니다."));

        if (routine.isActive()) {
            routine.deactivate();
        } else {
            routine.activate();
        }
    }

    @Transactional(readOnly = true)
    public List<RoutineResponse> getRoutines(Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<Routine> routines = routineRepository.findAllRoutinesByMember(member);
        return routines.stream()
                .map(RoutineResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 날짜 범위에 대한 루틴 미리보기 조회
    @Transactional(readOnly = true)
    public List<RoutinePreviewResponse> getRoutineForDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<Routine> activeRoutines = routineRepository.findActiveRoutinesByMember(member);
        
        return activeRoutines.stream()
                .flatMap(routine -> startDate.datesUntil(endDate.plusDays(1))
                        .filter(date -> routine.isApplicableOn(date.atStartOfDay()))
                        .map(date -> RoutinePreviewResponse.from(routine, date)))
                .collect(Collectors.toList());
    }

    // 루틴을 실제 Todo로 변환
    @Transactional
    public void convertRoutineToTodo(Long memberId, Long routineId, LocalDate targetDate) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Routine routine = routineRepository.findByIdAndMember(routineId, member)
                .orElseThrow(() -> new NotFoundException("루틴을 찾을 수 없습니다."));

        if (!routine.isActive()) {
            throw new ValidationException("비활성화된 루틴입니다.");
        }

        if (!routine.isApplicableOn(targetDate.atStartOfDay())) {
            throw new ValidationException("해당 날짜에 적용할 수 없는 루틴입니다.");
        }

        // 이미 생성된 Todo가 있는지 확인
        LocalDateTime targetDateTime = routine.getDateTimeFor(targetDate.atStartOfDay());
        boolean todoExists = todoRepository.existsByMemberAndRoutineAndDueDate(member, routine, targetDateTime);
        
        if (todoExists) {
            throw new ValidationException("이미 해당 날짜에 생성된 Todo가 있습니다.");
        }

        Todo todo = Todo.createFromRoutine(routine, targetDateTime, member);
        todoRepository.save(todo);
    }
}
