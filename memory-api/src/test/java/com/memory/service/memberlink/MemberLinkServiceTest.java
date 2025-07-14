package com.memory.service.memberlink;

import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.memberlink.MemberLink;
import com.memory.domain.memberlink.repository.MemberLinkRepository;
import com.memory.dto.memberlink.MemberLinkRequest;
import com.memory.dto.memberlink.response.MemberLinkResponse;
import com.memory.dto.memberlink.response.MemberPublicLinkResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberLinkServiceTest {

    @Mock
    private MemberLinkRepository memberLinkRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberLinkService memberLinkService;

    private Member member;
    private MemberLink memberLink;
    private MemberLinkRequest.Create createRequest;
    private MemberLinkRequest.Update updateRequest;
    private MemberLinkRequest.UpdateOrder updateOrderRequest;

    private final Long memberId = 1L;
    private final Long linkId = 1L;
    private final String title = "테스트 링크";
    private final String url = "https://example.com";
    private final String description = "테스트 설명";
    private final String iconUrl = "https://example.com/icon.png";
    private final Integer displayOrder = 1;

    @BeforeEach
    void setUp() {
        // Member 객체 생성
        member = new Member("테스트 사용자", "testuser", "test@example.com", "encodedPassword");
        setId(member, memberId);

        // MemberLink 객체 생성
        memberLink = MemberLink.builder()
                .member(member)
                .title(title)
                .url(url)
                .description(description)
                .displayOrder(displayOrder)
                .isActive(true)
                .isVisible(true)
                .iconUrl(iconUrl)
                .build();
        setId(memberLink, linkId);

        // Request 객체들 생성
        createRequest = createMemberLinkCreateRequest();
        updateRequest = createMemberLinkUpdateRequest();
        updateOrderRequest = createUpdateOrderRequest();
    }

    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    private MemberLinkRequest.Create createMemberLinkCreateRequest() {
        try {
            MemberLinkRequest.Create request = new MemberLinkRequest.Create();
            setFieldValue(request, "title", title);
            setFieldValue(request, "url", url);
            setFieldValue(request, "description", description);
            setFieldValue(request, "isActive", true);
            setFieldValue(request, "isVisible", true);
            setFieldValue(request, "iconUrl", iconUrl);
            return request;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request", e);
        }
    }

    private MemberLinkRequest.Update createMemberLinkUpdateRequest() {
        try {
            MemberLinkRequest.Update request = new MemberLinkRequest.Update();
            setFieldValue(request, "title", "수정된 제목");
            setFieldValue(request, "url", "https://modified.com");
            setFieldValue(request, "description", "수정된 설명");
            setFieldValue(request, "displayOrder", 2);
            setFieldValue(request, "isActive", false);
            setFieldValue(request, "isVisible", false);
            setFieldValue(request, "iconUrl", "https://modified.com/icon.png");
            return request;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request", e);
        }
    }

    private MemberLinkRequest.UpdateOrder createUpdateOrderRequest() {
        try {
            MemberLinkRequest.UpdateOrder request = new MemberLinkRequest.UpdateOrder();
            setFieldValue(request, "displayOrder", 2);
            return request;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request", e);
        }
    }

    private void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Test
    @DisplayName("멤버 링크 생성 성공 테스트")
    void createMemberLinkSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memberLinkRepository.findMaxDisplayOrderByMemberId(memberId)).thenReturn(0);
        when(memberLinkRepository.save(any(MemberLink.class))).thenReturn(memberLink);

        // When
        MemberLinkResponse response = memberLinkService.createMemberLink(memberId, createRequest);

        // Then
        assertNotNull(response);
        assertEquals(linkId, response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(url, response.getUrl());
        assertEquals(description, response.getDescription());
        assertEquals(true, response.getIsActive());
        assertEquals(true, response.getIsVisible());
        assertEquals(iconUrl, response.getIconUrl());

        verify(memberRepository).findMemberById(memberId);
        verify(memberLinkRepository).findMaxDisplayOrderByMemberId(memberId);
        verify(memberLinkRepository).save(any(MemberLink.class));
    }

    @Test
    @DisplayName("멤버 링크 생성 실패 테스트 - 존재하지 않는 회원")
    void createMemberLinkFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memberLinkService.createMemberLink(memberId, createRequest));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memberLinkRepository, never()).save(any(MemberLink.class));
    }

    @Test
    @DisplayName("멤버 링크 수정 성공 테스트")
    void updateMemberLinkSuccess() {
        // Given
        when(memberLinkRepository.findByIdAndMemberId(linkId, memberId)).thenReturn(Optional.of(memberLink));

        // When
        MemberLinkResponse response = memberLinkService.updateMemberLink(memberId, linkId, updateRequest);

        // Then
        assertNotNull(response);
        verify(memberLinkRepository).findByIdAndMemberId(linkId, memberId);
    }

    @Test
    @DisplayName("멤버 링크 수정 실패 테스트 - 존재하지 않는 링크")
    void updateMemberLinkFailLinkNotFound() {
        // Given
        when(memberLinkRepository.findByIdAndMemberId(linkId, memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memberLinkService.updateMemberLink(memberId, linkId, updateRequest));

        assertEquals("링크를 찾을 수 없거나 수정 권한이 없습니다.", exception.getMessage());
        verify(memberLinkRepository).findByIdAndMemberId(linkId, memberId);
    }

    @Test
    @DisplayName("멤버 링크 순서 변경 성공 테스트 - 동일한 순서")
    void updateMemberLinkOrderSuccessSameOrder() {
        // Given
        when(memberLinkRepository.findByIdAndMemberId(linkId, memberId)).thenReturn(Optional.of(memberLink));
        when(memberLinkRepository.countByMemberId(memberId)).thenReturn(3L);

        MemberLinkRequest.UpdateOrder sameOrderRequest;
        try {
            sameOrderRequest = new MemberLinkRequest.UpdateOrder();
            setFieldValue(sameOrderRequest, "displayOrder", 1); // 현재와 동일한 순서
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request", e);
        }

        // When
        MemberLinkResponse response = memberLinkService.updateMemberLinkOrder(memberId, linkId, sameOrderRequest);

        // Then
        assertNotNull(response);
        verify(memberLinkRepository).findByIdAndMemberId(linkId, memberId);
        verify(memberLinkRepository).countByMemberId(memberId);
        verify(memberLinkRepository, never()).findByMemberIdAndDisplayOrderBetween(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("멤버 링크 순서 변경 성공 테스트 - 뒤로 이동")
    void updateMemberLinkOrderSuccessMoveBackward() {
        // Given
        when(memberLinkRepository.findByIdAndMemberId(linkId, memberId)).thenReturn(Optional.of(memberLink));
        when(memberLinkRepository.countByMemberId(memberId)).thenReturn(3L);

        MemberLink link2 = MemberLink.builder()
                .member(member)
                .title("링크2")
                .url("https://link2.com")
                .displayOrder(2)
                .isActive(true)
                .isVisible(true)
                .build();
        setId(link2, 2L);

        MemberLink link3 = MemberLink.builder()
                .member(member)
                .title("링크3")
                .url("https://link3.com")
                .displayOrder(3)
                .isActive(true)
                .isVisible(true)
                .build();
        setId(link3, 3L);

        when(memberLinkRepository.findByMemberIdAndDisplayOrderBetween(memberId, 2, 3))
                .thenReturn(Arrays.asList(link2, link3));

        MemberLinkRequest.UpdateOrder moveBackwardRequest;
        try {
            moveBackwardRequest = new MemberLinkRequest.UpdateOrder();
            setFieldValue(moveBackwardRequest, "displayOrder", 3); // 1 -> 3으로 이동
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request", e);
        }

        // When
        MemberLinkResponse response = memberLinkService.updateMemberLinkOrder(memberId, linkId, moveBackwardRequest);

        // Then
        assertNotNull(response);
        verify(memberLinkRepository).findByIdAndMemberId(linkId, memberId);
        verify(memberLinkRepository).countByMemberId(memberId);
        verify(memberLinkRepository).findByMemberIdAndDisplayOrderBetween(memberId, 2, 3);
    }

    @Test
    @DisplayName("멤버 링크 순서 변경 실패 테스트 - 유효하지 않은 순서")
    void updateMemberLinkOrderFailInvalidOrder() {
        // Given
        when(memberLinkRepository.findByIdAndMemberId(linkId, memberId)).thenReturn(Optional.of(memberLink));
        when(memberLinkRepository.countByMemberId(memberId)).thenReturn(3L);

        MemberLinkRequest.UpdateOrder invalidOrderRequest;
        try {
            invalidOrderRequest = new MemberLinkRequest.UpdateOrder();
            setFieldValue(invalidOrderRequest, "displayOrder", 5); // 유효 범위(1-3) 초과
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request", e);
        }

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> memberLinkService.updateMemberLinkOrder(memberId, linkId, invalidOrderRequest));

        assertEquals("유효하지 않은 순서입니다. (1 ~ 3)", exception.getMessage());
        verify(memberLinkRepository).findByIdAndMemberId(linkId, memberId);
        verify(memberLinkRepository).countByMemberId(memberId);
    }

    @Test
    @DisplayName("멤버 링크 목록 조회 성공 테스트")
    void getMemberLinksSuccess() {
        // Given
        MemberLink link2 = MemberLink.builder()
                .member(member)
                .title("링크2")
                .url("https://link2.com")
                .displayOrder(2)
                .isActive(true)
                .isVisible(true)
                .build();
        setId(link2, 2L);

        List<MemberLink> memberLinks = Arrays.asList(memberLink, link2);
        when(memberLinkRepository.findActiveByMemberIdOrderByDisplayOrder(memberId)).thenReturn(memberLinks);

        // When
        List<MemberLinkResponse> responses = memberLinkService.getMemberLinks(memberId);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(linkId, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());
        verify(memberLinkRepository).findActiveByMemberIdOrderByDisplayOrder(memberId);
    }

    @Test
    @DisplayName("공개 멤버 링크 목록 조회 성공 테스트")
    void getPublicMemberLinksSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));

        List<MemberLink> publicLinks = Collections.singletonList(memberLink);
        when(memberLinkRepository.findPublicByMemberIdOrderByDisplayOrder(memberId)).thenReturn(publicLinks);

        // When
        MemberPublicLinkResponse response = memberLinkService.getPublicMemberLinks(memberId);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(memberLinkRepository).findPublicByMemberIdOrderByDisplayOrder(memberId);
    }

    @Test
    @DisplayName("공개 멤버 링크 목록 조회 실패 테스트 - 존재하지 않는 회원")
    void getPublicMemberLinksFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memberLinkService.getPublicMemberLinks(memberId));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memberLinkRepository, never()).findPublicByMemberIdOrderByDisplayOrder(anyLong());
    }

    @Test
    @DisplayName("멤버 링크 삭제 성공 테스트")
    void deleteMemberLinkSuccess() {
        // Given
        when(memberLinkRepository.findByIdAndMemberId(linkId, memberId)).thenReturn(Optional.of(memberLink));

        // When
        assertDoesNotThrow(() -> memberLinkService.deleteMemberLink(memberId, linkId));

        // Then
        verify(memberLinkRepository).findByIdAndMemberId(linkId, memberId);
    }

    @Test
    @DisplayName("멤버 링크 삭제 실패 테스트 - 존재하지 않는 링크")
    void deleteMemberLinkFailLinkNotFound() {
        // Given
        when(memberLinkRepository.findByIdAndMemberId(linkId, memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memberLinkService.deleteMemberLink(memberId, linkId));

        assertEquals("링크를 찾을 수 없거나 삭제 권한이 없습니다.", exception.getMessage());
        verify(memberLinkRepository).findByIdAndMemberId(linkId, memberId);
    }

    @Test
    @DisplayName("클릭 수 증가 성공 테스트")
    void incrementClickCountSuccess() {
        // Given
        when(memberLinkRepository.findById(linkId)).thenReturn(Optional.of(memberLink));

        // When
        MemberLinkResponse response = memberLinkService.incrementClickCount(linkId);

        // Then
        assertNotNull(response);
        verify(memberLinkRepository).findById(linkId);
    }

    @Test
    @DisplayName("클릭 수 증가 실패 테스트 - 존재하지 않는 링크")
    void incrementClickCountFailLinkNotFound() {
        // Given
        when(memberLinkRepository.findById(linkId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memberLinkService.incrementClickCount(linkId));

        assertEquals("링크를 찾을 수 없습니다.", exception.getMessage());
        verify(memberLinkRepository).findById(linkId);
    }

    @Test
    @DisplayName("클릭 수 증가 실패 테스트 - 삭제된 링크")
    void incrementClickCountFailDeletedLink() {
        // Given
        // 삭제된 링크 시뮬레이션 (isDeleted() 메소드가 true를 반환하도록)
        MemberLink deletedLink = spy(memberLink);
        when(deletedLink.isDeleted()).thenReturn(true);
        when(memberLinkRepository.findById(linkId)).thenReturn(Optional.of(deletedLink));

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memberLinkService.incrementClickCount(linkId));

        assertEquals("삭제된 링크입니다.", exception.getMessage());
        verify(memberLinkRepository).findById(linkId);
    }

    @Test
    @DisplayName("클릭 수 증가 실패 테스트 - 접근할 수 없는 링크")
    void incrementClickCountFailInaccessibleLink() {
        // Given
        // 접근할 수 없는 링크 시뮬레이션
        MemberLink inaccessibleLink = spy(memberLink);
        when(inaccessibleLink.isDeleted()).thenReturn(false);
        when(inaccessibleLink.isAccessible()).thenReturn(false);
        when(memberLinkRepository.findById(linkId)).thenReturn(Optional.of(inaccessibleLink));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> memberLinkService.incrementClickCount(linkId));

        assertEquals("접근할 수 없는 링크입니다.", exception.getMessage());
        verify(memberLinkRepository).findById(linkId);
    }
}
