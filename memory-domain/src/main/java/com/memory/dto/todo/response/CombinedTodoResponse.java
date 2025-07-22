package com.memory.dto.todo.response;

import com.memory.dto.routine.response.RoutinePreviewResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CombinedTodoResponse {
    private List<TodoResponse> actualTodos;        // 실제로 생성된 Todo들
    private List<RoutinePreviewResponse> routinePreviews; // 루틴 미리보기들 (흐릿하게 표시할 항목)
}
