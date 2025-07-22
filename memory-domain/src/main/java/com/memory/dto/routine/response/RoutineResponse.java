package com.memory.dto.routine.response;

import com.memory.domain.routine.Routine;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RoutineResponse {
    private Long id;
    private String title;
    private String content;
    private boolean active;
    private String repeatType;
    private Integer interval;
    private LocalDate startDate;
    private LocalDate endDate;

    public static RoutineResponse from(Routine routine) {
        return RoutineResponse.builder()
                .id(routine.getId())
                .title(routine.getTitle())
                .content(routine.getContent())
                .active(routine.isActive())
                .repeatType(routine.getRepeatSetting().getRepeatType().name())
                .interval(routine.getRepeatSetting().getInterval())
                .startDate(routine.getRepeatSetting().getStartDate())
                .endDate(routine.getRepeatSetting().getEndDate())
                .build();
    }
}
