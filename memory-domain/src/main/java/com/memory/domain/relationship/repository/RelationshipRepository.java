package com.memory.domain.relationship.repository;

import com.memory.domain.relationship.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
}