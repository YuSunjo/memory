package com.memory.service.member;

import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.file.File;
import com.memory.domain.file.repository.FileRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
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
    private final FileRepository fileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MemberResponse signup(MemberRequest.Signup signupRequestDto) {
        memberRepository.findMemberByEmailAndMemberType(signupRequestDto.getEmail(), MemberType.MEMBER)
                .ifPresent(member -> {
                    throw new ConflictException("이미 존재하는 이메일입니다.");
                });

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        Member member = memberRepository.save(signupRequestDto.toEntity(encodedPassword));
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberLoginResponse login(MemberRequest.Login loginRequestDto) {
        Member member = memberRepository.findMemberByEmailAndMemberType(loginRequestDto.getEmail(), MemberType.MEMBER)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new ValidationException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());
        return MemberLoginResponse.of(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public MemberResponse findMemberById(Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse updateMember(Long memberId, MemberRequest.Update updateRequestDto) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        File file = fileRepository.findById(updateRequestDto.getFileId())
                .orElseThrow(() -> new NotFoundException("파일을 찾을 수 없습니다."));

        if (file.validateMember(memberId)) {
            throw new ValidationException("이미 다른 회원과 연결된 파일입니다.");
        }

        member.update(updateRequestDto.getNickname(), file);

        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse updatePassword(Long memberId, MemberRequest.PasswordUpdate passwordUpdateRequestDto) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        String encodedPassword = passwordEncoder.encode(passwordUpdateRequestDto.getPassword());
        member.updatePassword(encodedPassword);

        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse findMemberByEmail(String email) {
        Member member = memberRepository.findMemberByEmailAndMemberType(email, MemberType.MEMBER)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이메일입니다."));
        return MemberResponse.from(member);
    }
}
