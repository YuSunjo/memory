package com.memory.controller.map;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.map.MapRequest;
import com.memory.dto.map.response.MapResponse;
import com.memory.domain.map.MapType;
import com.memory.exception.customException.ValidationException;
import com.memory.response.ServerResponse;
import com.memory.service.map.MapService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Map", description = "Map API")
public class MapController {

    private final MapService mapService;

    @ApiOperations.SecuredApi(
        summary = "지도 생성",
        description = "새로운 지도를 생성합니다.",
        response = MapResponse.class
    )
    @Auth
    @PostMapping("api/v1/maps")
    public ServerResponse<MapResponse> createMap(
            @RequestBody @Valid MapRequest.Create createRequest, @MemberId Long memberId) {
        return ServerResponse.success(mapService.createMap(createRequest, memberId));
    }

    @ApiOperations.BasicApi(
        summary = "지도 조회",
        description = "지도 ID로 지도를 조회합니다.",
        response = MapResponse.class
    )
    @GetMapping("api/v1/maps/{mapId}")
    public ServerResponse<MapResponse> findMapById(
            @PathVariable Long mapId) {
        return ServerResponse.success(mapService.findMapById(mapId));
    }

    @ApiOperations.BasicApi(
        summary = "지도 목록 조회",
        description = "모든 지도의 목록을 조회합니다.(USER_PLACE 타입 제외)",
        response = MapResponse.class
    )
    @GetMapping("api/v1/maps")
    public ServerResponse<List<MapResponse>> findMapsByType(
            @RequestParam(required = false, defaultValue = "FESTIVAL") MapType mapType) {
        if (mapType == MapType.USER_PLACE) {
            throw new ValidationException("USER_PLACE 타입의 지도는 조회할 수 없습니다.");
        }
        return ServerResponse.success(mapService.findMapsByType(mapType));
    }

    @ApiOperations.SecuredApi(
        summary = "회원의 지도 목록 조회",
        description = "회원의 지도 목록을 조회합니다.",
        response = MapResponse.class
    )
    @Auth
    @GetMapping("api/v1/maps/member")
    public ServerResponse<List<MapResponse>> findMapsByMember(
            @Parameter(hidden = true) @MemberId Long memberId) {
        return ServerResponse.success(mapService.findMapsByMemberAndType(memberId));
    }

}
