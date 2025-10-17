package com.memory.service.admin

import com.memory.config.jwt.JwtTokenProvider
import com.memory.domain.member.MemberType
import com.memory.domain.member.repository.MemberRepository
import com.memory.dto.member.MemberRequest
import com.memory.dto.member.response.MemberLoginResponse
import com.memory.exception.customException.NotFoundException
import com.memory.exception.customException.ValidationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberAdminService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @Transactional(readOnly = true)
    fun adminLogin(request: MemberRequest.Login): MemberLoginResponse {
        val member = memberRepository.findMemberByEmailAndMemberType(request.email, MemberType.ADMIN)
            .orElseThrow { NotFoundException("존재하지 않는 회원입니다.") }

        if (!passwordEncoder.matches(request.password, member.password)) {
            throw ValidationException("비밀번호가 일치하지 않습니다.")
        }
        val accessToken = jwtTokenProvider.createAccessToken(member.email)
        val refreshToken = jwtTokenProvider.createRefreshToken(member.email)

        return MemberLoginResponse.of(accessToken, refreshToken)
    }
}