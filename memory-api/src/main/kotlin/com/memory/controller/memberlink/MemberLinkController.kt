package com.memory.controller.memberlink

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.BasicApi
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.dto.memberlink.MemberLinkRequest
import com.memory.dto.memberlink.response.MemberLinkResponse
import com.memory.dto.memberlink.response.MemberPublicLinkResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.memberlink.MemberLinkService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "MemberLink", description = "회원 링크 관리 API")
class MemberLinkController(
    private val memberLinkService: MemberLinkService,
) {
    @SecuredApi(
        summary = "링크 생성",
        description = "회원의 새로운 링크를 생성합니다. 링크는 자동으로 목록의 마지막에 추가됩니다.",
        response = MemberLinkResponse::class
    )
    @Auth
    @PostMapping("/api/v1/member-links")
    fun createMemberLink(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestBody request: @Valid MemberLinkRequest.Create
    ): ServerResponse<MemberLinkResponse?> {
        val response = memberLinkService.createMemberLink(memberId, request)
        return success(response)
    }

    @SecuredApi(
        summary = "링크 수정",
        description = "회원의 기존 링크 정보를 수정합니다. 본인의 링크만 수정할 수 있습니다.",
        response = MemberLinkResponse::class
    )
    @Auth
    @PutMapping("/api/v1/member-links/{linkId}")
    fun updateMemberLink(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable linkId: Long?,
        @RequestBody request: @Valid MemberLinkRequest.Update
    ): ServerResponse<MemberLinkResponse?> {
        val response = memberLinkService.updateMemberLink(memberId, linkId, request)
        return success(response)
    }

    @SecuredApi(
        summary = "링크 순서 변경",
        description = "회원의 링크 순서를 변경합니다. 다른 링크들의 순서도 자동으로 재배치됩니다.",
        response = MemberLinkResponse::class
    )
    @Auth
    @PatchMapping("/api/v1/member-links/{linkId}/order")
    fun updateMemberLinkOrder(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable linkId: Long?,
        @RequestBody request: @Valid MemberLinkRequest.UpdateOrder
    ): ServerResponse<MemberLinkResponse?> {
        val response = memberLinkService.updateMemberLinkOrder(memberId, linkId, request)
        return success(response)
    }

    @SecuredApi(
        summary = "내 링크 목록 조회",
        description = "로그인한 회원의 링크 목록을 조회합니다. 비공개 링크도 포함됩니다.",
        response = MemberLinkResponse::class
    )
    @Auth
    @GetMapping("/api/v1/member-links")
    fun getMemberLinks(
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<List<MemberLinkResponse?>?> {
        val response: List<MemberLinkResponse?> = memberLinkService.getMemberLinks(memberId)
        return success(response)
    }

    @BasicApi(
        summary = "공개 링크 목록 조회",
        description = "특정 회원의 공개 링크 목록을 조회합니다. 인증 없이 접근 가능합니다.",
        response = MemberLinkResponse::class
    )
    @GetMapping("/api/v1/members/{memberId}/links")
    fun getPublicMemberLinks(
        @PathVariable memberId: Long?
    ): ServerResponse<MemberPublicLinkResponse?> {
        return success(memberLinkService.getPublicMemberLinks(memberId))
    }

    @SecuredApi(summary = "링크 삭제", description = "회원의 링크를 삭제합니다. 소프트 딜리트로 처리되며, 본인의 링크만 삭제할 수 있습니다.")
    @Auth
    @DeleteMapping("/api/v1/member-links/{linkId}")
    fun deleteMemberLink(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable linkId: Long?
    ): ServerResponse<String> {
        memberLinkService.deleteMemberLink(memberId, linkId)
        return ServerResponse.OK
    }

    @BasicApi(
        summary = "링크 클릭 카운트 증가",
        description = "공개 링크 클릭 시 카운트를 증가시킵니다. 인증 없이 접근 가능합니다.",
        response = MemberLinkResponse::class
    )
    @PostMapping("/api/v1/member-links/{linkId}/click")
    fun incrementClickCount(
        @PathVariable linkId: Long
    ): ServerResponse<MemberLinkResponse?> {
        val response = memberLinkService.incrementClickCount(linkId)
        return success(response)
    }
}
