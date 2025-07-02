package com.memory.domain.memberlink.repository;

import com.memory.domain.memberlink.MemberLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLinkRepository extends JpaRepository<MemberLink, Long>, MemberLinkRepositoryCustom {
}
