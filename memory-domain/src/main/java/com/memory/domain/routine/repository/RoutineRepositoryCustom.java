package com.memory.domain.routine.repository;

import com.memory.domain.member.Member;
import com.memory.domain.routine.Routine;

import java.util.List;
import java.util.Optional;

public interface RoutineRepositoryCustom {
    
    /**
     * 회원의 활성화된 루틴 목록 조회
     */
    List<Routine> findActiveRoutinesByMember(Member member);
    
    /**
     * 회원의 모든 루틴 목록 조회 (삭제되지 않은 것만)
     */
    List<Routine> findAllRoutinesByMember(Member member);
    
    /**
     * ID와 회원으로 루틴 조회
     */
    Optional<Routine> findByIdAndMember(Long id, Member member);
    
    /**
     * 중복 루틴 존재 여부 확인 (마이그레이션용)
     */
    boolean existsByMemberAndTitleAndRepeatType(Member member, String title, String repeatType);
    
    /**
     * 활성화된 루틴 개수 조회
     */
    long countActiveRoutinesByMember(Member member);
    
    /**
     * 특정 반복 타입의 루틴 목록 조회
     */
    List<Routine> findActiveRoutinesByMemberAndRepeatType(Member member, String repeatType);
}
