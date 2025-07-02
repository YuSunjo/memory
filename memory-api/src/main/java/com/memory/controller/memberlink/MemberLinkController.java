package com.memory.controller.memberlink;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.memberlink.MemberLinkRequest;
import com.memory.dto.memberlink.response.MemberLinkResponse;
import com.memory.response.ServerResponse;
import com.memory.service.memberlink.MemberLinkService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "MemberLink", description = "회원 링크 관리 API")
public class MemberLinkController {

    private final MemberLinkService memberLinkService;

    @ApiOperations.SecuredApi(
            summary = "링크 생성",
            description = "회원의 새로운 링크를 생성합니다. 링크는 자동으로 목록의 마지막에 추가됩니다.",
            response = MemberLinkResponse.class
    )
    @Auth
    @PostMapping("/api/v1/member-links")
    public ServerResponse<MemberLinkResponse> createMemberLink(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestBody @Valid MemberLinkRequest.Create request) {
        MemberLinkResponse response = memberLinkService.createMemberLink(memberId, request);
        return ServerResponse.success(response);
    }

    @ApiOperations.SecuredApi(
            summary = "링크 수정",
            description = "회원의 기존 링크 정보를 수정합니다. 본인의 링크만 수정할 수 있습니다.",
            response = MemberLinkResponse.class
    )
    @Auth
    @PutMapping("/api/v1/member-links/{linkId}")
    public ServerResponse<MemberLinkResponse> updateMemberLink(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long linkId,
            @RequestBody @Valid MemberLinkRequest.Update request) {
        MemberLinkResponse response = memberLinkService.updateMemberLink(memberId, linkId, request);
        return ServerResponse.success(response);
    }

    @ApiOperations.SecuredApi(
            summary = "링크 순서 변경",
            description = "회원의 링크 순서를 변경합니다. 다른 링크들의 순서도 자동으로 재배치됩니다.",
            response = MemberLinkResponse.class
    )
    @Auth
    @PatchMapping("/api/v1/member-links/{linkId}/order")
    public ServerResponse<MemberLinkResponse> updateMemberLinkOrder(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long linkId,
            @RequestBody @Valid MemberLinkRequest.UpdateOrder request) {
        MemberLinkResponse response = memberLinkService.updateMemberLinkOrder(memberId, linkId, request);
        return ServerResponse.success(response);
    }

    @ApiOperations.SecuredApi(
            summary = "내 링크 목록 조회",
            description = "로그인한 회원의 링크 목록을 조회합니다. 비공개 링크도 포함됩니다.",
            response = MemberLinkResponse.class
    )
    @Auth
    @GetMapping("/api/v1/member-links")
    public ServerResponse<List<MemberLinkResponse>> getMemberLinks(
            @Parameter(hidden = true) @MemberId Long memberId) {
        List<MemberLinkResponse> response = memberLinkService.getMemberLinks(memberId);
        return ServerResponse.success(response);
    }

    @ApiOperations.BasicApi(
            summary = "공개 링크 목록 조회",
            description = "특정 회원의 공개 링크 목록을 조회합니다. 인증 없이 접근 가능합니다.",
            response = MemberLinkResponse.class
    )
    @GetMapping("/api/v1/members/{memberId}/links")
    public ServerResponse<List<MemberLinkResponse>> getPublicMemberLinks(
            @PathVariable Long memberId) {
        List<MemberLinkResponse> response = memberLinkService.getPublicMemberLinks(memberId);
        return ServerResponse.success(response);
    }

    @ApiOperations.SecuredApi(
            summary = "링크 삭제",
            description = "회원의 링크를 삭제합니다. 소프트 딜리트로 처리되며, 본인의 링크만 삭제할 수 있습니다."
    )
    @Auth
    @DeleteMapping("/api/v1/member-links/{linkId}")
    public ServerResponse<String> deleteMemberLink(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long linkId) {
        
        log.info("Request to delete member link for member: {}, linkId: {}", memberId, linkId);
        
        memberLinkService.deleteMemberLink(memberId, linkId);
        return ServerResponse.OK;
    }

    @ApiOperations.BasicApi(
            summary = "링크 클릭 카운트 증가",
            description = "공개 링크 클릭 시 카운트를 증가시킵니다. 인증 없이 접근 가능합니다.",
            response = MemberLinkResponse.class
    )
    @PostMapping("/api/v1/member-links/{linkId}/click")
    public ServerResponse<MemberLinkResponse> incrementClickCount(
            @PathVariable Long linkId) {
        MemberLinkResponse response = memberLinkService.incrementClickCount(linkId);
        return ServerResponse.success(response);
    }
}
