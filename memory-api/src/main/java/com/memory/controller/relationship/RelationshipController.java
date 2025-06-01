package com.memory.controller.relationship;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.domain.relationship.RelationshipStatus;
import com.memory.dto.relationship.RelationshipRequest;
import com.memory.dto.relationship.response.RelationshipListResponse;
import com.memory.dto.relationship.response.RelationshipResponse;
import com.memory.response.ServerResponse;
import com.memory.service.relationship.RelationshipService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Relationship", description = "Relationship API")
public class RelationshipController {

    private final RelationshipService relationshipService;

    @ApiOperations.SecuredApi(
            summary = "관계 요청 생성",
            description = "새로운 관계 요청을 생성합니다.",
            response = RelationshipResponse.class
    )
    @Auth
    @PostMapping("api/v1/relationship/request")
    public ServerResponse<RelationshipResponse> createRelationshipRequest(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestBody @Valid RelationshipRequest.Create createRequestDto) {
        return ServerResponse.success(relationshipService.createRelationshipRequest(memberId, createRequestDto));
    }

    @ApiOperations.SecuredApi(
            summary = "관계 요청 수락",
            description = "기존 관계 요청을 수락합니다.",
            response = RelationshipResponse.class
    )
    @Auth
    @PostMapping("api/v1/relationship/accept/{relationshipId}")
    public ServerResponse<RelationshipResponse> acceptRelationshipRequest(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long relationshipId) {
        return ServerResponse.success(relationshipService.acceptRelationshipRequest(memberId, relationshipId));
    }

    @ApiOperations.SecuredApi(
            summary = "관계 목록 조회",
            description = "내가 요청한 모든 관계를 조회합니다.",
            response = RelationshipListResponse.class
    )
    @Auth
    @GetMapping("api/v1/relationship")
    public ServerResponse<RelationshipListResponse> getRelationships(
            @Parameter(hidden = true) @MemberId Long memberId) {
        return ServerResponse.success(relationshipService.getRelationships(memberId));
    }

    @ApiOperations.SecuredApi(
            summary = "관계 상태별 조회",
            description = "특정 상태의 관계를 조회합니다.",
            response = RelationshipListResponse.class
    )
    @Auth
    @GetMapping("api/v1/relationship/status")
    public ServerResponse<RelationshipListResponse> getRelationshipsByStatus(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestParam("status") String status) {
        return ServerResponse.success(relationshipService.getRelationshipsByStatus(memberId, RelationshipStatus.valueOf(status)));
    }

    @ApiOperations.SecuredApi(
            summary = "관계 종료",
            description = "관계를 종료합니다.",
            response = RelationshipResponse.class
    )
    @Auth
    @PostMapping("api/v1/relationship/end/{relationshipId}")
    public ServerResponse<RelationshipResponse> endRelationship(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long relationshipId) {
        return ServerResponse.success(relationshipService.endRelationship(memberId, relationshipId));
    }
}
