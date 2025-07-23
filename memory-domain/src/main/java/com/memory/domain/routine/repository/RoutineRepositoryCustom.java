package com.memory.domain.routine.repository;

import com.memory.domain.member.Member;
import com.memory.domain.routine.Routine;

import java.util.List;
import java.util.Optional;

public interface RoutineRepositoryCustom {
    
    List<Routine> findActiveRoutinesByMember(Member member);
    
    List<Routine> findAllRoutinesByMember(Member member);
    
    Optional<Routine> findByIdAndMember(Long id, Member member);
}
