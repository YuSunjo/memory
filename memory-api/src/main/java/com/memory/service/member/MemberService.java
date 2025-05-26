package com.memory.service.member;

import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.member.MemberRequest;
import com.memory.dto.member.response.MemberLoginResponse;
import com.memory.dto.member.response.MemberResponse;
import com.memory.exception.customException.ConflictException;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MemberResponse signup(MemberRequest.Signup signupRequestDto) {
        memberRepository.findMemberByEmail(signupRequestDto.getEmail())
                .ifPresent(member -> {
                    throw new ConflictException("이미 존재하는 이메일입니다.");
                });

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        Member member = memberRepository.save(signupRequestDto.toEntity(encodedPassword));
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberLoginResponse login(MemberRequest.Login loginRequestDto) {
        Member member = memberRepository.findMemberByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new ValidationException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());
        return MemberLoginResponse.of(accessToken, refreshToken);
    }
}
