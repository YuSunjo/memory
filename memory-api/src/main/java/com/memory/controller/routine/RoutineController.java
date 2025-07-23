package com.memory.controller.routine;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.routine.RoutineRequest;
import com.memory.dto.routine.response.RoutineResponse;
import com.memory.response.ServerResponse;
import com.memory.service.routine.RoutineService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Routine", description = "Routine API")
public class RoutineController {

    private final RoutineService routineService;

    @ApiOperations.SecuredApi(
        summary = "루틴 생성",
        description = "새로운 루틴을 생성합니다.",
        response = RoutineResponse.class
    )
    @Auth
    @PostMapping("/api/v1/routine")
    public ServerResponse<RoutineResponse> createRoutine(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestBody @Valid RoutineRequest.Create request) {
        return ServerResponse.success(routineService.createRoutine(memberId, request));
    }

    @ApiOperations.SecuredApi(
        summary = "루틴 수정",
        description = "기존 루틴을 수정합니다.",
        response = RoutineResponse.class
    )
    @Auth
    @PutMapping("/api/v1/routine/{routineId}")
    public ServerResponse<RoutineResponse> updateRoutine(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long routineId,
            @RequestBody @Valid RoutineRequest.Update request) {
        return ServerResponse.success(routineService.updateRoutine(memberId, routineId, request));
    }

    @ApiOperations.SecuredApi(
        summary = "루틴 삭제",
        description = "루틴을 삭제합니다."
    )
    @Auth
    @DeleteMapping("/api/v1/routine/{routineId}")
    public ServerResponse<String> deleteRoutine(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long routineId) {
        routineService.deleteRoutine(memberId, routineId);
        return ServerResponse.OK;
    }

    @ApiOperations.SecuredApi(
        summary = "루틴 활성화/비활성화",
        description = "루틴의 활성화 상태를 토글합니다."
    )
    @Auth
    @PatchMapping("/api/v1/routine/{routineId}/toggle")
    public ServerResponse<String> toggleRoutineActive(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long routineId) {
        routineService.toggleRoutineActive(memberId, routineId);
        return ServerResponse.OK;
    }

    @ApiOperations.SecuredApi(
        summary = "루틴 목록 조회",
        description = "사용자의 모든 루틴을 조회합니다.",
        response = RoutineResponse.class
    )
    @Auth
    @GetMapping("/api/v1/routine")
    public ServerResponse<List<RoutineResponse>> getRoutines(
            @Parameter(hidden = true) @MemberId Long memberId) {
        return ServerResponse.success(routineService.getRoutines(memberId));
    }

}
