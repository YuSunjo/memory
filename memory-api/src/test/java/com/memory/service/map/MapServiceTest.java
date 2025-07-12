package com.memory.service.map;

import com.memory.domain.map.Map;
import com.memory.domain.map.MapType;
import com.memory.domain.map.repository.MapRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.map.MapRequest;
import com.memory.dto.map.response.MapResponse;
import com.memory.exception.customException.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MapServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MapRepository mapRepository;

    @InjectMocks
    private MapService mapService;

    private Member member;
    private Map mapEntity;
    private MapRequest.Create createRequest;

    private final Long memberId = 1L;
    private final Long mapId = 1L;
    private final String mapName = "테스트 맵";
    private final String description = "테스트 맵 설명";
    private final String address = "서울특별시 중구 명동";
    private final String latitude = "37.5665";
    private final String longitude = "126.978";
    private final MapType mapType = MapType.USER_PLACE;

    @BeforeEach
    void setUp() {
        // Member 객체 생성
        member = new Member("테스트 사용자", "testuser", "test@example.com", "encodedPassword");
        setId(member, memberId);

        // Map 객체 생성
        mapEntity = new Map(mapName, description, address, latitude, longitude, mapType, member);
        setId(mapEntity, mapId);

        // Request 객체 생성
        createRequest = new MapRequest.Create(mapName, description, address, latitude, longitude, mapType);
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

    @Test
    @DisplayName("지도 생성 성공 테스트")
    void createMapSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(mapRepository.save(any(Map.class))).thenReturn(mapEntity);

        // When
        MapResponse response = mapService.createMap(createRequest, memberId);

        // Then
        assertNotNull(response);
        assertEquals(mapId, response.id());
        assertEquals(mapName, response.name());
        assertEquals(description, response.description());
        assertEquals(address, response.address());
        assertEquals(latitude, response.latitude());
        assertEquals(longitude, response.longitude());
        assertEquals(mapType, response.mapType());

        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository).save(any(Map.class));
    }

    @Test
    @DisplayName("지도 생성 실패 테스트 - 존재하지 않는 회원")
    void createMapFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> mapService.createMap(createRequest, memberId));

        assertEquals("회원이 존재하지 않습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository, never()).save(any(Map.class));
    }

    @Test
    @DisplayName("지도 조회 성공 테스트")
    void findMapByIdSuccess() {
        // Given
        when(mapRepository.findById(mapId)).thenReturn(Optional.of(mapEntity));

        // When
        MapResponse response = mapService.findMapById(mapId);

        // Then
        assertNotNull(response);
        assertEquals(mapId, response.id());
        assertEquals(mapName, response.name());
        assertEquals(description, response.description());
        assertEquals(address, response.address());
        assertEquals(latitude, response.latitude());
        assertEquals(longitude, response.longitude());
        assertEquals(mapType, response.mapType());

        verify(mapRepository).findById(mapId);
    }

    @Test
    @DisplayName("지도 조회 실패 테스트 - 존재하지 않는 지도")
    void findMapByIdFailMapNotFound() {
        // Given
        when(mapRepository.findById(mapId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> mapService.findMapById(mapId));

        assertEquals("지도를 찾을 수 없습니다.", exception.getMessage());
        verify(mapRepository).findById(mapId);
    }

    @Test
    @DisplayName("지도 타입별 조회 성공 테스트")
    void findMapsByTypeSuccess() {
        // Given
        Map publicMap1 = new Map("사용자 장소 1", "사용자 장소 1 설명", "서울시 강남구", 
                "37.5172", "127.0473", MapType.USER_PLACE, member);
        setId(publicMap1, 2L);

        Map publicMap2 = new Map("축제 장소", "축제 장소 설명", "서울시 서초구", 
                "37.4837", "127.0324", MapType.FESTIVAL, member);
        setId(publicMap2, 3L);

        List<Map> userPlaceMaps = Arrays.asList(mapEntity, publicMap1);

        when(mapRepository.findByMapType(MapType.USER_PLACE)).thenReturn(userPlaceMaps);

        // When
        List<MapResponse> responses = mapService.findMapsByType(MapType.USER_PLACE);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(mapId, responses.get(0).id());
        assertEquals(2L, responses.get(1).id());
        assertTrue(responses.stream().allMatch(map -> map.mapType() == MapType.USER_PLACE));

        verify(mapRepository).findByMapType(MapType.USER_PLACE);
    }

    @Test
    @DisplayName("지도 타입별 조회 테스트 - 빈 결과")
    void findMapsByTypeEmptyResult() {
        // Given
        when(mapRepository.findByMapType(MapType.FESTIVAL)).thenReturn(List.of());

        // When
        List<MapResponse> responses = mapService.findMapsByType(MapType.FESTIVAL);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(mapRepository).findByMapType(MapType.FESTIVAL);
    }

    @Test
    @DisplayName("회원별 지도 조회 성공 테스트")
    void findMapsByMemberAndTypeSuccess() {
        // Given
        Map festivalMap = new Map("축제 맵", "축제 맵 설명", "서울시 마포구", 
                "37.5663", "126.9779", MapType.FESTIVAL, member);
        setId(festivalMap, 4L);

        List<Map> memberMaps = Arrays.asList(mapEntity, festivalMap);

        when(mapRepository.findByMemberId(memberId)).thenReturn(memberMaps);

        // When
        List<MapResponse> responses = mapService.findMapsByMemberAndType(memberId);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(mapId, responses.get(0).id());
        assertEquals(4L, responses.get(1).id());

        verify(mapRepository).findByMemberId(memberId);
    }

    @Test
    @DisplayName("회원별 지도 조회 테스트 - 빈 결과")
    void findMapsByMemberAndTypeEmptyResult() {
        // Given
        when(mapRepository.findByMemberId(memberId)).thenReturn(List.of());

        // When
        List<MapResponse> responses = mapService.findMapsByMemberAndType(memberId);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(mapRepository).findByMemberId(memberId);
    }

    @Test
    @DisplayName("다양한 지도 타입으로 생성 테스트")
    void createMapWithDifferentTypes() {
        // Given
        MapRequest.Create festivalMapRequest = new MapRequest.Create("축제 맵", "축제 설명", 
                "서울시 종로구", "37.5735", "126.9788", MapType.FESTIVAL);

        Map festivalMapEntity = new Map("축제 맵", "축제 설명", "서울시 종로구", 
                "37.5735", "126.9788", MapType.FESTIVAL, member);
        setId(festivalMapEntity, 5L);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(mapRepository.save(any(Map.class))).thenReturn(festivalMapEntity);

        // When
        MapResponse response = mapService.createMap(festivalMapRequest, memberId);

        // Then
        assertNotNull(response);
        assertEquals(5L, response.id());
        assertEquals("축제 맵", response.name());
        assertEquals(MapType.FESTIVAL, response.mapType());

        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository).save(any(Map.class));
    }

    @Test
    @DisplayName("설명 없이 지도 생성 테스트")
    void createMapWithoutDescription() {
        // Given
        MapRequest.Create requestWithoutDesc = new MapRequest.Create(mapName, null, address, 
                latitude, longitude, mapType);

        Map mapWithoutDesc = new Map(mapName, null, address, latitude, longitude, mapType, member);
        setId(mapWithoutDesc, 6L);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(mapRepository.save(any(Map.class))).thenReturn(mapWithoutDesc);

        // When
        MapResponse response = mapService.createMap(requestWithoutDesc, memberId);

        // Then
        assertNotNull(response);
        assertEquals(6L, response.id());
        assertEquals(mapName, response.name());
        assertNull(response.description());

        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository).save(any(Map.class));
    }
}
