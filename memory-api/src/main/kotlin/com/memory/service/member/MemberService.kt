package com.memory.service.member

import com.memory.config.jwt.JwtTokenProvider
import com.memory.domain.file.repository.FileRepository
import com.memory.domain.member.Member
import com.memory.domain.member.MemberType
import com.memory.domain.member.repository.MemberRepository
import com.memory.dto.member.MemberRequest.*
import com.memory.dto.member.response.MemberLoginResponse
import com.memory.dto.member.response.MemberLoginResponse.Companion.of
import com.memory.dto.member.response.MemberResponse
import com.memory.dto.member.response.MemberResponse.Companion.from
import com.memory.exception.customException.ConflictException
import com.memory.exception.customException.NotFoundException
import com.memory.exception.customException.ValidationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Consumer

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val fileRepository: FileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @Transactional
    fun signup(signupRequestDto: Signup): MemberResponse {
        memberRepository.findMemberByEmailAndMemberType(signupRequestDto.email, MemberType.MEMBER)
            .ifPresent(Consumer { member: Member? ->
                throw ConflictException("이미 존재하는 이메일입니다.")
            })

        val encodedPassword = passwordEncoder.encode(signupRequestDto.password)
        val member = memberRepository.save<Member>(signupRequestDto.toEntity(encodedPassword))
        return from(member)
    }

    @Transactional(readOnly = true)
    fun login(loginRequestDto: Login): MemberLoginResponse {
        val member = memberRepository.findMemberByEmailAndMemberType(loginRequestDto.email, MemberType.MEMBER)
            .orElseThrow{ NotFoundException("존재하지 않는 이메일입니다.") }

        if (!passwordEncoder.matches(loginRequestDto.password, member.password)) {
            throw ValidationException("비밀번호가 일치하지 않습니다.")
        }

        val accessToken = jwtTokenProvider.createAccessToken(member.email)
        val refreshToken = jwtTokenProvider.createRefreshToken(member.email)
        return of(accessToken, refreshToken)
    }

    @Transactional(readOnly = true)
    fun findMemberById(memberId: Long?): MemberResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }
        return from(member)
    }

    @Transactional
    fun updateMember(memberId: Long, updateRequestDto: Update): MemberResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val file = fileRepository.findById(updateRequestDto.fileId)
            .orElseThrow { NotFoundException("파일을 찾을 수 없습니다.") }

        if (file.validateMember(memberId)) {
            throw ValidationException("이미 다른 회원과 연결된 파일입니다.")
        }

        member.update(updateRequestDto.nickname, file)

        return from(member)
    }

    @Transactional
    fun updatePassword(memberId: Long?, passwordUpdateRequestDto: PasswordUpdate): MemberResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val encodedPassword = passwordEncoder.encode(passwordUpdateRequestDto.password)
        member.updatePassword(encodedPassword)

        return from(member)
    }

    @Transactional(readOnly = true)
    fun findMemberByEmail(email: String?): MemberResponse {
        val member = memberRepository.findMemberByEmailAndMemberType(email, MemberType.MEMBER)
            .orElseThrow { NotFoundException("존재하지 않는 이메일입니다.") }
        return from(member)
    }
}
