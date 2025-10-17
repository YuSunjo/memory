package com.memory.controller.map

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.BasicApi
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.domain.map.MapType
import com.memory.dto.map.MapRequest
import com.memory.dto.map.response.MapResponse
import com.memory.exception.customException.ValidationException
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.map.MapService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Map", description = "Map API")
class MapController(
    private val mapService: MapService,
) {
    @SecuredApi(summary = "지도 생성", description = "새로운 지도를 생성합니다.", response = MapResponse::class)
    @Auth
    @PostMapping("api/v1/maps")
    fun createMap(
        @RequestBody createRequest: @Valid MapRequest.Create, @MemberId memberId: Long?
    ): ServerResponse<MapResponse?> {
        return success(mapService.createMap(createRequest, memberId))
    }

    @BasicApi(summary = "지도 조회", description = "지도 ID로 지도를 조회합니다.", response = MapResponse::class)
    @GetMapping("api/v1/maps/{mapId}")
    fun findMapById(
        @PathVariable mapId: Long
    ): ServerResponse<MapResponse?> {
        return success(mapService.findMapById(mapId))
    }

    @BasicApi(summary = "지도 목록 조회", description = "모든 지도의 목록을 조회합니다.(USER_PLACE 타입 제외)", response = MapResponse::class)
    @GetMapping("api/v1/maps")
    fun findMapsByType(
        @RequestParam(required = false, defaultValue = "FESTIVAL") mapType: MapType?
    ): ServerResponse<List<MapResponse>?> {
        if (mapType == MapType.USER_PLACE) {
            throw ValidationException("USER_PLACE 타입의 지도는 조회할 수 없습니다.")
        }
        return success(mapService.findMapsByType(mapType))
    }

    @SecuredApi(summary = "회원의 지도 목록 조회", description = "회원의 지도 목록을 조회합니다.", response = MapResponse::class)
    @Auth
    @GetMapping("api/v1/maps/member")
    fun findMapsByMember(
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<List<MapResponse>?> {
        return success(mapService.findMapsByMemberAndType(memberId))
    }
}
