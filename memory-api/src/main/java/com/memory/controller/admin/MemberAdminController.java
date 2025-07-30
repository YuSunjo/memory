package com.memory.controller.admin;

import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.member.MemberRequest;
import com.memory.dto.member.response.MemberLoginResponse;
import com.memory.response.ServerResponse;
import com.memory.service.admin.MemberAdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Member", description = "관리자 회원 API")
public class MemberAdminController {

    private final MemberAdminService memberAdminService;

    @PostMapping("/login")
    @ApiOperations.BasicApi(
        summary = "관리자 로그인",
        description = "관리자 권한을 가진 회원만 로그인할 수 있습니다. 관리자 토큰을 발급합니다.",
        response = MemberLoginResponse.class
    )
    public ServerResponse<MemberLoginResponse> adminLogin(@Valid @RequestBody MemberRequest.Login loginRequest) {
        MemberLoginResponse response = memberAdminService.adminLogin(loginRequest);
        return ServerResponse.success(response);
    }
}