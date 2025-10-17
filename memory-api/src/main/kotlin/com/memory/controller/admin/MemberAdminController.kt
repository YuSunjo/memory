package com.memory.controller.admin

import com.memory.annotation.swagger.ApiOperations.BasicApi
import com.memory.dto.member.MemberRequest.Login
import com.memory.dto.member.response.MemberLoginResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.admin.MemberAdminService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Admin Member", description = "관리자 회원 API")
class MemberAdminController(
    private val memberAdminService: MemberAdminService
) {
    @PostMapping("/api/v1/admin/login")
    @BasicApi(
        summary = "관리자 로그인",
        description = "관리자 권한을 가진 회원만 로그인할 수 있습니다. 관리자 토큰을 발급합니다.",
        response = MemberLoginResponse::class
    )
    fun adminLogin(@RequestBody loginRequest: @Valid Login): ServerResponse<MemberLoginResponse?> {
        val response = memberAdminService.adminLogin(loginRequest)
        return success(response)
    }
}