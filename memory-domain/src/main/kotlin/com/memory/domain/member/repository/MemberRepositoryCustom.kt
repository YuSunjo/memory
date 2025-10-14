package com.memory.domain.member.repository

import com.memory.domain.member.Member
import com.memory.domain.member.MemberType
import java.util.Optional

interface MemberRepositoryCustom {

    fun findMemberById(memberId: Long?): Optional<Member>

    fun findMemberByEmailAndMemberType(email: String?, memberType: MemberType?): Optional<Member>
}
