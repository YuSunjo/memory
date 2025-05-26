package com.memory.controller.member;

import com.memory.ApiResponse;
import com.memory.dto.member.MemberRequest;
import com.memory.dto.member.MemberResponse;
import com.memory.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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


}
