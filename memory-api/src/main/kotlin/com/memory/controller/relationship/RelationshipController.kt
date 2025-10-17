package com.memory.controller.relationship

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.domain.relationship.RelationshipStatus
import com.memory.dto.relationship.RelationshipRequest
import com.memory.dto.relationship.response.RelationshipListResponse
import com.memory.dto.relationship.response.RelationshipResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.relationship.RelationshipService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Relationship", description = "Relationship API")
class RelationshipController(
    private val relationshipService: RelationshipService,
) {
    @SecuredApi(summary = "관계 요청 생성", description = "새로운 관계 요청을 생성합니다.", response = RelationshipResponse::class)
    @Auth
    @PostMapping("api/v1/relationship/request")
    fun createRelationshipRequest(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestBody createRequestDto: @Valid RelationshipRequest.Create
    ): ServerResponse<RelationshipResponse?> {
        return success(
            relationshipService.createRelationshipRequest(
                memberId,
                createRequestDto
            )
        )
    }

    @SecuredApi(summary = "관계 요청 수락", description = "기존 관계 요청을 수락합니다.", response = RelationshipResponse::class)
    @Auth
    @PostMapping("api/v1/relationship/accept/{relationshipId}")
    fun acceptRelationshipRequest(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable relationshipId: Long
    ): ServerResponse<RelationshipResponse?> {
        return success(relationshipService.acceptRelationshipRequest(memberId, relationshipId))
    }

    @SecuredApi(summary = "관계 목록 조회", description = "내가 요청한 모든 관계를 조회합니다.", response = RelationshipListResponse::class)
    @Auth
    @GetMapping("api/v1/relationship")
    fun getRelationships(
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<RelationshipListResponse?> {
        return success(relationshipService.getRelationships(memberId))
    }

    @SecuredApi(summary = "관계 상태별 조회", description = "특정 상태의 관계를 조회합니다.", response = RelationshipListResponse::class)
    @Auth
    @GetMapping("api/v1/relationship/status")
    fun getRelationshipsByStatus(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestParam("status") status: String
    ): ServerResponse<RelationshipListResponse?> {
        return success(
            relationshipService.getRelationshipsByStatus(
                memberId,
                RelationshipStatus.valueOf(status)
            )
        )
    }

    @SecuredApi(
        summary = "받은 관계 목록 조회",
        description = "내가 받은 모든 관계를 조회합니다.",
        response = RelationshipListResponse::class
    )
    @Auth
    @GetMapping("api/v1/relationship/received")
    fun getReceivedRelationships(
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<RelationshipListResponse?> {
        return success(relationshipService.getReceivedRelationships(memberId))
    }

    @SecuredApi(
        summary = "받은 관계 상태별 조회",
        description = "특정 상태의 받은 관계를 조회합니다.",
        response = RelationshipListResponse::class
    )
    @Auth
    @GetMapping("api/v1/relationship/received/status")
    fun getReceivedRelationshipsByStatus(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestParam("status") status: String
    ): ServerResponse<RelationshipListResponse?> {
        return success(
            relationshipService.getReceivedRelationshipsByStatus(
                memberId,
                RelationshipStatus.valueOf(status)
            )
        )
    }

    @SecuredApi(summary = "관계 종료", description = "관계를 종료합니다.", response = RelationshipResponse::class)
    @Auth
    @PostMapping("api/v1/relationship/end/{relationshipId}")
    fun endRelationship(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable relationshipId: Long
    ): ServerResponse<RelationshipResponse?> {
        return success(relationshipService.endRelationship(memberId, relationshipId))
    }
}
