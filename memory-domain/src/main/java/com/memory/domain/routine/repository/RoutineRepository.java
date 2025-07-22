package com.memory.domain.routine.repository;

import com.memory.domain.routine.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Long>, RoutineRepositoryCustom {
    
}
