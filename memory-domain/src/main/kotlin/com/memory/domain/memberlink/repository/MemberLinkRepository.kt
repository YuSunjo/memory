package com.memory.domain.memberlink.repository

import com.memory.domain.memberlink.MemberLink
import org.springframework.data.jpa.repository.JpaRepository

interface MemberLinkRepository : JpaRepository<MemberLink, Long>, MemberLinkRepositoryCustom
