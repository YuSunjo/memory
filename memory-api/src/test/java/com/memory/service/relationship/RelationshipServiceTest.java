package com.memory.service.relationship;

import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.relationship.Relationship;
import com.memory.domain.relationship.RelationshipStatus;
import com.memory.domain.relationship.repository.RelationshipRepository;
import com.memory.dto.relationship.RelationshipRequest;
import com.memory.dto.relationship.response.RelationshipListResponse;
import com.memory.dto.relationship.response.RelationshipResponse;
import com.memory.exception.customException.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelationshipServiceTest {

    @Mock
    private RelationshipRepository relationshipRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RelationshipService relationshipService;

    private Member member1;
    private Member member2;
    private Relationship pendingRelationship;
    private Relationship acceptedRelationship;
    private RelationshipRequest.Create createRequest;

    private final Long member1Id = 1L;
    private final Long member2Id = 2L;
    private final Long relationshipId = 1L;

    @BeforeEach
    void setUp() {
        // Create test members
        member1 = new Member("Member 1", "member1", "member1@example.com", "password1", "http://example.com/profile1.jpg");
        member2 = new Member("Member 2", "member2", "member2@example.com", "password2", "http://example.com/profile2.jpg");

        // Set IDs using reflection
        setId(member1, member1Id);
        setId(member2, member2Id);

        // Create test relationships
        pendingRelationship = Relationship.createRelationship(member1, member2, RelationshipStatus.PENDING);
        acceptedRelationship = Relationship.createRelationship(member1, member2, RelationshipStatus.ACCEPTED);

        // Set ID for relationship
        setId(pendingRelationship, relationshipId);

        // Create request DTO
        createRequest = new RelationshipRequest.Create(member2Id);
    }

    private void setId(Object object, Long id) {
        try {
            java.lang.reflect.Field idField = object.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(object, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @Test
    @DisplayName("관계 요청 생성 성공 테스트")
    void createRelationshipRequestSuccess() {
        // Given
        when(memberRepository.findMemberById(member1Id)).thenReturn(Optional.of(member1));
        when(memberRepository.findMemberById(member2Id)).thenReturn(Optional.of(member2));

        // Mock the save method to set the ID and return the relationship
        when(relationshipRepository.save(any(Relationship.class))).thenAnswer(invocation -> {
            Relationship savedRelationship = invocation.getArgument(0);
            setId(savedRelationship, relationshipId);
            return savedRelationship;
        });

        // When
        RelationshipResponse response = relationshipService.createRelationshipRequest(member1Id, createRequest);

        // Then
        assertNotNull(response);
        assertEquals(relationshipId, response.id());
        assertEquals(member1Id, response.member().id());
        assertEquals(member2Id, response.relatedMember().id());
        assertEquals(RelationshipStatus.PENDING, response.relationshipStatus());

        verify(memberRepository).findMemberById(member1Id);
        verify(memberRepository).findMemberById(member2Id);
        verify(relationshipRepository).save(any(Relationship.class));
    }

    @Test
    @DisplayName("관계 요청 생성 실패 테스트 - 요청 회원 없음")
    void createRelationshipRequestFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(member1Id)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> relationshipService.createRelationshipRequest(member1Id, createRequest));

        assertEquals("요청한 회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(member1Id);
        verify(memberRepository, never()).findMemberById(member2Id);
        verify(relationshipRepository, never()).save(any(Relationship.class));
    }

    @Test
    @DisplayName("관계 요청 생성 실패 테스트 - 대상 회원 없음")
    void createRelationshipRequestFailRelatedMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(member1Id)).thenReturn(Optional.of(member1));
        when(memberRepository.findMemberById(member2Id)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> relationshipService.createRelationshipRequest(member1Id, createRequest));

        assertEquals("대상 회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(member1Id);
        verify(memberRepository).findMemberById(member2Id);
        verify(relationshipRepository, never()).save(any(Relationship.class));
    }

    @Test
    @DisplayName("관계 요청 수락 성공 테스트")
    void acceptRelationshipRequestSuccess() {
        // Given
        when(memberRepository.findMemberById(member2Id)).thenReturn(Optional.of(member2));
        when(relationshipRepository.findById(relationshipId)).thenReturn(Optional.of(pendingRelationship));
        when(relationshipRepository.save(any(Relationship.class))).thenReturn(acceptedRelationship);

        // When
        RelationshipResponse response = relationshipService.acceptRelationshipRequest(member2Id, relationshipId);

        // Then
        assertNotNull(response);
        assertEquals(relationshipId, response.id());
        assertEquals(member1Id, response.member().id());
        assertEquals(member2Id, response.relatedMember().id());
        assertEquals(RelationshipStatus.ACCEPTED, response.relationshipStatus());

        verify(memberRepository).findMemberById(member2Id);
        verify(relationshipRepository).findById(relationshipId);
        verify(relationshipRepository).save(any(Relationship.class));
    }

    @Test
    @DisplayName("관계 요청 수락 실패 테스트 - 회원 없음")
    void acceptRelationshipRequestFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(member2Id)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> relationshipService.acceptRelationshipRequest(member2Id, relationshipId));

        assertEquals("요청한 회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(member2Id);
        verify(relationshipRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("관계 요청 수락 실패 테스트 - 관계 요청 없음")
    void acceptRelationshipRequestFailRelationshipNotFound() {
        // Given
        when(memberRepository.findMemberById(member2Id)).thenReturn(Optional.of(member2));
        when(relationshipRepository.findById(relationshipId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> relationshipService.acceptRelationshipRequest(member2Id, relationshipId));

        assertEquals("관계 요청을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(member2Id);
        verify(relationshipRepository).findById(relationshipId);
        verify(relationshipRepository, never()).save(any(Relationship.class));
    }

    @Test
    @DisplayName("관계 요청 수락 실패 테스트 - 권한 없음")
    void acceptRelationshipRequestFailNoPermission() {
        // Given
        Member otherMember = new Member("Other", "other", "other@example.com", "password", "http://example.com/other.jpg");
        setId(otherMember, 3L);

        when(memberRepository.findMemberById(3L)).thenReturn(Optional.of(otherMember));
        when(relationshipRepository.findById(relationshipId)).thenReturn(Optional.of(pendingRelationship));

        // When & Then
        assertThrows(Exception.class,
            () -> relationshipService.acceptRelationshipRequest(3L, relationshipId));

        verify(memberRepository).findMemberById(3L);
        verify(relationshipRepository).findById(relationshipId);
        verify(relationshipRepository, never()).save(any(Relationship.class));
    }

    @Test
    @DisplayName("관계 목록 조회 성공 테스트")
    void getRelationshipsSuccess() {
        // Given
        List<Relationship> relationships = Arrays.asList(pendingRelationship, acceptedRelationship);
        when(memberRepository.findMemberById(member1Id)).thenReturn(Optional.of(member1));
        when(relationshipRepository.findByMember(member1)).thenReturn(relationships);

        // When
        RelationshipListResponse response = relationshipService.getRelationships(member1Id);

        // Then
        assertNotNull(response);
        assertEquals(2, response.relationships().size());

        verify(memberRepository).findMemberById(member1Id);
        verify(relationshipRepository).findByMember(member1);
    }

    @Test
    @DisplayName("상태별 관계 목록 조회 성공 테스트")
    void getRelationshipsByStatusSuccess() {
        // Given
        List<Relationship> pendingRelationships = Collections.singletonList(pendingRelationship);
        when(memberRepository.findMemberById(member1Id)).thenReturn(Optional.of(member1));
        when(relationshipRepository.findByMemberAndRelationshipStatus(member1, RelationshipStatus.PENDING))
            .thenReturn(pendingRelationships);

        // When
        RelationshipListResponse response = relationshipService.getRelationshipsByStatus(member1Id, RelationshipStatus.PENDING);

        // Then
        assertNotNull(response);
        assertEquals(1, response.relationships().size());
        assertEquals(RelationshipStatus.PENDING, response.relationships().get(0).relationshipStatus());

        verify(memberRepository).findMemberById(member1Id);
        verify(relationshipRepository).findByMemberAndRelationshipStatus(member1, RelationshipStatus.PENDING);
    }

    @Test
    @DisplayName("관계 종료 성공 테스트")
    void endRelationshipSuccess() {
        // Given
        when(memberRepository.findMemberById(member1Id)).thenReturn(Optional.of(member1));

        // Set ID for acceptedRelationship
        setId(acceptedRelationship, relationshipId);
        when(relationshipRepository.findById(relationshipId)).thenReturn(Optional.of(acceptedRelationship));

        Relationship reciprocalRelationship = Relationship.createRelationship(member2, member1, RelationshipStatus.ACCEPTED);
        when(relationshipRepository.findByMemberIdAndRelatedMemberId(member2Id, member1Id))
            .thenReturn(Collections.singletonList(reciprocalRelationship));

        // When
        RelationshipResponse response = relationshipService.endRelationship(member1Id, relationshipId);

        // Then
        assertNotNull(response);
        assertEquals(relationshipId, response.id());
        assertEquals(RelationshipStatus.ENDED, response.relationshipStatus());

        verify(memberRepository).findMemberById(member1Id);
        verify(relationshipRepository).findById(relationshipId);
        verify(relationshipRepository).findByMemberIdAndRelatedMemberId(member2Id, member1Id);
    }

    @Test
    @DisplayName("관계 종료 실패 테스트 - 회원 없음")
    void endRelationshipFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(member1Id)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> relationshipService.endRelationship(member1Id, relationshipId));

        assertEquals("요청한 회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(member1Id);
        verify(relationshipRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("관계 종료 실패 테스트 - 관계 없음")
    void endRelationshipFailRelationshipNotFound() {
        // Given
        when(memberRepository.findMemberById(member1Id)).thenReturn(Optional.of(member1));
        when(relationshipRepository.findById(relationshipId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> relationshipService.endRelationship(member1Id, relationshipId));

        assertEquals("관계를 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(member1Id);
        verify(relationshipRepository).findById(relationshipId);
        verify(relationshipRepository, never()).findByMemberIdAndRelatedMemberId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("관계 종료 실패 테스트 - 권한 없음")
    void endRelationshipFailNoPermission() {
        // Given
        Member otherMember = new Member("Other", "other", "other@example.com", "password", "http://example.com/other.jpg");
        setId(otherMember, 3L);

        when(memberRepository.findMemberById(3L)).thenReturn(Optional.of(otherMember));
        when(relationshipRepository.findById(relationshipId)).thenReturn(Optional.of(acceptedRelationship));

        // When & Then
        assertThrows(Exception.class,
            () -> relationshipService.endRelationship(3L, relationshipId));

        verify(memberRepository).findMemberById(3L);
        verify(relationshipRepository).findById(relationshipId);
        verify(relationshipRepository, never()).findByMemberIdAndRelatedMemberId(anyLong(), anyLong());
    }
}
