package com.memory.controller.member;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.dto.member.response.MemberLoginResponse;
import com.memory.response.ServerResponse;
import com.memory.dto.member.MemberRequest;
import com.memory.dto.member.response.MemberResponse;
import com.memory.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Member", description = "Member API")
public class MemberController {

    private final MemberService memberService;

    @Operation(
        summary = "회원 가입",
        description = "새로운 회원을 등록합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "회원 가입 성공",
            content = @Content(schema = @Schema(implementation = MemberResponse.class))
        ),
    })
    @PostMapping("api/v1/member/signup")
    public ServerResponse<MemberResponse> signUp(@RequestBody @Valid MemberRequest.Signup signupRequestDto) {
        return ServerResponse.success(memberService.signup(signupRequestDto));
    }

    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호로 로그인합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = MemberLoginResponse.class))
        ),
    })
    @PostMapping("api/v1/member/login")
    public ServerResponse<MemberLoginResponse> login(@RequestBody @Valid MemberRequest.Login loginRequestDto) {
        return ServerResponse.success(memberService.login(loginRequestDto));
    }

    @Operation(
        summary = "회원 정보 조회",
        description = "JWT 토큰에서 추출한 memberId로 회원 정보를 조회합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "회원 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = MemberResponse.class))
        ),
    })
    @Auth
    @GetMapping("api/v1/member/me")
    public ServerResponse<MemberResponse> findMember(
            @Parameter(hidden = true) @MemberId Long memberId) {
        return ServerResponse.success(memberService.findMemberById(memberId));
    }

    @Operation(
        summary = "회원 정보 수정",
        description = "회원의 이름, 닉네임, 프로필 이미지 URL을 수정합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "회원 정보 수정 성공",
            content = @Content(schema = @Schema(implementation = MemberResponse.class))
        ),
    })
    @Auth
    @PutMapping("api/v1/member/me")
    public ServerResponse<MemberResponse> updateMember(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestBody MemberRequest.Update updateRequestDto) {
        return ServerResponse.success(memberService.updateMember(memberId, updateRequestDto));
    }

    @Operation(
        summary = "비밀번호 수정",
        description = "회원의 비밀번호를 수정합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "비밀번호 수정 성공",
            content = @Content(schema = @Schema(implementation = MemberResponse.class))
        ),
    })
    @Auth
    @PutMapping("api/v1/member/me/password")
    public ServerResponse<MemberResponse> updatePassword(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestBody @Valid MemberRequest.PasswordUpdate passwordUpdateRequestDto) {
        return ServerResponse.success(memberService.updatePassword(memberId, passwordUpdateRequestDto));
    }

    @Operation(
        summary = "이메일로 회원 조회",
        description = "이메일로 회원을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "회원 조회 성공",
            content = @Content(schema = @Schema(implementation = MemberResponse.class))
        ),
    })
    @GetMapping("api/v1/member/email")
    public ServerResponse<MemberResponse> findMemberByEmail(
            @RequestParam("email") String email) {
        return ServerResponse.success(memberService.findMemberByEmail(email));
    }
}
