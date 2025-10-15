package com.memory.dto.routine.response;

import com.memory.domain.routine.Routine;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RoutinePreviewResponse {
    private Long routineId;
    private String title;
    private String content;
    private LocalDate targetDate;
    private boolean isPreview; // UI에서 흐릿하게 표시하기 위한 플래그

    public static RoutinePreviewResponse from(Routine routine, LocalDate targetDate) {
        return RoutinePreviewResponse.builder()
                .routineId(routine.getId())
                .title(routine.getTitle())
                .content(routine.getContent())
                .targetDate(targetDate)
                .isPreview(true)
                .build();
    }
}
