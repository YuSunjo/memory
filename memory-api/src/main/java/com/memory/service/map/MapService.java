package com.memory.service.map;

import com.memory.domain.map.Map;
import com.memory.domain.map.MapType;
import com.memory.domain.map.repository.MapRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.map.MapRequest;
import com.memory.dto.map.response.MapResponse;
import com.memory.exception.customException.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MemberRepository memberRepository;
    private final MapRepository mapRepository;

    @Transactional
    public MapResponse createMap(MapRequest.Create createRequest, Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다."));
        Map savedMap = mapRepository.save(createRequest.toEntity(member));
        return MapResponse.from(savedMap);
    }

    @Transactional(readOnly = true)
    public MapResponse findMapById(Long mapId) {
        Map map = mapRepository.findById(mapId)
                .orElseThrow(() -> new NotFoundException("지도를 찾을 수 없습니다."));
        return MapResponse.from(map);
    }

    @Transactional(readOnly = true)
    public List<MapResponse> findMapsByType(MapType mapType) {
        List<Map> maps = mapRepository.findByMapType(mapType);
        return maps.stream()
                .map(MapResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MapResponse> findMapsByMemberAndType(Long memberId) {
        List<Map> maps = mapRepository.findByMemberId(memberId);
        return maps.stream()
                .map(MapResponse::from)
                .collect(Collectors.toList());
    }
}
