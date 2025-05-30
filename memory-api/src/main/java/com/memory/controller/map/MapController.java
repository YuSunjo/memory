package com.memory.controller.map;

import com.memory.annotation.Auth;
import com.memory.dto.map.MapRequest;
import com.memory.dto.map.response.MapResponse;
import com.memory.domain.map.MapType;
import com.memory.response.ServerResponse;
import com.memory.service.map.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(
        summary = "지도 생성",
        description = "새로운 지도를 생성합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "지도 생성 성공",
            content = @Content(schema = @Schema(implementation = MapResponse.class))
        ),
    })
    @Auth
    @PostMapping("api/v1/maps")
    public ServerResponse<MapResponse> createMap(
            @RequestBody @Valid MapRequest.Create createRequest) {
        return ServerResponse.success(mapService.createMap(createRequest));
    }

    @Operation(
        summary = "지도 조회",
        description = "ID로 지도를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "지도 조회 성공",
            content = @Content(schema = @Schema(implementation = MapResponse.class))
        ),
    })
    @GetMapping("api/v1/maps/{mapId}")
    public ServerResponse<MapResponse> findMapById(
            @PathVariable Long mapId) {
        return ServerResponse.success(mapService.findMapById(mapId));
    }

    @Operation(
        summary = "지도 타입별 조회",
        description = "지도 타입별로 지도를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "지도 조회 성공",
            content = @Content(schema = @Schema(implementation = MapResponse.class))
        ),
    })
    @GetMapping("api/v1/maps/type/{mapType}")
    public ServerResponse<List<MapResponse>> findMapsByType(
            @PathVariable MapType mapType) {
        return ServerResponse.success(mapService.findMapsByType(mapType));
    }

}