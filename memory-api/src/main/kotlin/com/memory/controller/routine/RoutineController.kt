package com.memory.controller.routine

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.dto.routine.RoutineRequest
import com.memory.dto.routine.response.RoutineResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.routine.RoutineService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Routine", description = "Routine API")
class RoutineController(
    private val routineService: RoutineService,
) {
    @SecuredApi(summary = "루틴 생성", description = "새로운 루틴을 생성합니다.", response = RoutineResponse::class)
    @Auth
    @PostMapping("/api/v1/routine")
    fun createRoutine(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestBody request: @Valid RoutineRequest.Create
    ): ServerResponse<RoutineResponse?> {
        return success(routineService.createRoutine(memberId, request))
    }

    @SecuredApi(summary = "루틴 수정", description = "기존 루틴을 수정합니다.", response = RoutineResponse::class)
    @Auth
    @PutMapping("/api/v1/routine/{routineId}")
    fun updateRoutine(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable routineId: Long?,
        @RequestBody request: @Valid RoutineRequest.Update
    ): ServerResponse<RoutineResponse?> {
        return success(routineService.updateRoutine(memberId, routineId, request))
    }

    @SecuredApi(summary = "루틴 삭제", description = "루틴을 삭제합니다.")
    @Auth
    @DeleteMapping("/api/v1/routine/{routineId}")
    fun deleteRoutine(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable routineId: Long?
    ): ServerResponse<String> {
        routineService.deleteRoutine(memberId, routineId)
        return ServerResponse.OK
    }

    @SecuredApi(summary = "루틴 활성화/비활성화", description = "루틴의 활성화 상태를 토글합니다.")
    @Auth
    @PatchMapping("/api/v1/routine/{routineId}/toggle")
    fun toggleRoutineActive(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable routineId: Long?
    ): ServerResponse<String> {
        routineService.toggleRoutineActive(memberId, routineId)
        return ServerResponse.OK
    }

    @SecuredApi(summary = "루틴 목록 조회", description = "사용자의 모든 루틴을 조회합니다.", response = RoutineResponse::class)
    @Auth
    @GetMapping("/api/v1/routine")
    fun getRoutines(
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<MutableList<RoutineResponse?>?> {
        return success(routineService.getRoutines(memberId))
    }
}
