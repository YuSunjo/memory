package com.memory.controller.member

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.BasicApi
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.dto.member.MemberRequest.*
import com.memory.dto.member.response.MemberLoginResponse
import com.memory.dto.member.response.MemberResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.member.MemberService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Member", description = "Member API")
class MemberController(
    private val memberService: MemberService,
) {
    @BasicApi(summary = "회원 가입", description = "새로운 회원을 등록합니다.", response = MemberResponse::class)
    @PostMapping("api/v1/member/signup")
    fun signUp(@RequestBody signupRequestDto: @Valid Signup): ServerResponse<MemberResponse?> {
        return success(memberService.signup(signupRequestDto))
    }

    @BasicApi(
        summary = "회원 로그인",
        description = "이메일과 비밀번호로 회원을 인증하고 JWT 토큰을 발급합니다.",
        response = MemberLoginResponse::class
    )
    @PostMapping("api/v1/member/login")
    fun login(@RequestBody loginRequestDto: @Valid Login): ServerResponse<MemberLoginResponse?> {
        return success(memberService.login(loginRequestDto))
    }

    @SecuredApi(summary = "내 정보 조회", description = "로그인한 회원의 정보를 조회합니다.", response = MemberResponse::class)
    @Auth
    @GetMapping("api/v1/member/me")
    fun findMember(
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<MemberResponse?> {
        return success(memberService.findMemberById(memberId))
    }

    @SecuredApi(summary = "회원 정보 수정", description = "로그인한 회원의 정보를 수정합니다.", response = MemberResponse::class)
    @Auth
    @PutMapping("api/v1/member/me")
    fun updateMember(
        @Parameter(hidden = true) @MemberId memberId: Long,
        @RequestBody updateRequestDto: Update
    ): ServerResponse<MemberResponse?> {
        return success(memberService.updateMember(memberId, updateRequestDto))
    }

    @SecuredApi(summary = "비밀번호 변경", description = "로그인한 회원의 비밀번호를 변경합니다.", response = MemberResponse::class)
    @Auth
    @PutMapping("api/v1/member/me/password")
    fun updatePassword(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestBody passwordUpdateRequestDto: @Valid PasswordUpdate
    ): ServerResponse<MemberResponse?> {
        return success(memberService.updatePassword(memberId, passwordUpdateRequestDto))
    }

    @BasicApi(summary = "이메일로 회원 조회", description = "이메일을 통해 회원 정보를 조회합니다.", response = MemberResponse::class)
    @GetMapping("api/v1/member/email")
    fun findMemberByEmail(
        @RequestParam("email") email: String?
    ): ServerResponse<MemberResponse?> {
        return success(memberService.findMemberByEmail(email))
    }
}
