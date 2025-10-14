package com.memory.domain.relationship.repository

import com.memory.domain.relationship.Relationship
import org.springframework.data.jpa.repository.JpaRepository

interface RelationshipRepository : JpaRepository<Relationship, Long>, RelationshipRepositoryCustom
