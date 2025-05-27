package com.memory.controller.member;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.dto.member.response.MemberLoginResponse;
import com.memory.response.ApiResponse;
import com.memory.dto.member.MemberRequest;
import com.memory.dto.member.response.MemberResponse;
import com.memory.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("api/v1/member/signup")
    public ApiResponse<MemberResponse> signUp(@RequestBody @Valid MemberRequest.Signup signupRequestDto) {
        return ApiResponse.success(memberService.signup(signupRequestDto));
    }

    @PostMapping("api/v1/member/login")
    public ApiResponse<MemberLoginResponse> login(@RequestBody @Valid MemberRequest.Login loginRequestDto) {
        return ApiResponse.success(memberService.login(loginRequestDto));
    }

    @Auth
    @GetMapping("api/v1/member/me")
    public ApiResponse<MemberResponse> findMember(@MemberId Long memberId) {
        return ApiResponse.success(memberService.findMemberById(memberId));
    }

}
