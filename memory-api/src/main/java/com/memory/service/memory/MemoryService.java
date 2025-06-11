package com.memory.service.memory;

import com.memory.domain.map.Map;
import com.memory.domain.map.repository.MapRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.memory.Memory;
import com.memory.domain.memory.MemoryType;
import com.memory.domain.memory.repository.MemoryRepository;
import com.memory.dto.memory.MemoryRequest;
import com.memory.dto.memory.response.MemoryResponse;
import com.memory.exception.customException.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final MemoryRepository memoryRepository;
    private final MemberRepository memberRepository;
    private final MapRepository mapRepository;

    private static final int DEFAULT_PAGE_SIZE = 10;

    @Transactional
    public MemoryResponse createMemory(Long memberId, MemoryRequest.Create createRequest) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Map map = mapRepository.findById(createRequest.getMapId())
                .orElseThrow(() -> new NotFoundException("지도를 찾을 수 없습니다."));

        Memory savedMemory = memoryRepository.save(createRequest.toEntity(member, map));
        return MemoryResponse.from(savedMemory);
    }

    @Transactional(readOnly = true)
    public MemoryResponse findMemoryById(Long memberId, Long memoryId) {
        memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Memory memory = memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)
                .orElseThrow(() -> new NotFoundException("해당 유저의 메모리를 찾을 수 없습니다."));

        return MemoryResponse.from(memory);
    }

    @Transactional(readOnly = true)
    public List<MemoryResponse> findMemoriesByMember(Long memberId, Long lastMemoryId, Integer size, MemoryType memoryType) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<Memory> memories;
        if (lastMemoryId == null) {
            memories = memoryRepository.findByMemberAndMemoryType(member, memoryType);
        } else {
            memories = memoryRepository.findByMemberAndMemoryType(member, memoryType, lastMemoryId, size);
        }

        return memories.stream()
                .map(MemoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemoryResponse updateMemory(Long memberId, Long memoryId, MemoryRequest.Update updateRequest) {
        memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Memory memory = memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)
                .orElseThrow(() -> new NotFoundException("해당 유저의 메모리를 찾을 수 없습니다."));

        // Check if the memory belongs to the member
        if (!memory.getMember().getId().equals(memberId)) {
            throw new NotFoundException("해당 메모리에 접근 권한이 없습니다.");
        }

        memory.update(updateRequest.getTitle(), updateRequest.getContent(), updateRequest.getLocationName(), updateRequest.getMemoryType());

        return MemoryResponse.from(memory);
    }

    @Transactional
    public void deleteMemory(Long memberId, Long memoryId) {
        memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Memory memory = memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)
                .orElseThrow(() -> new NotFoundException("해당 유저의 메모리를 찾을 수 없습니다."));

        if (!memory.getMember().getId().equals(memberId)) {
            throw new NotFoundException("해당 메모리에 접근 권한이 없습니다.");
        }

        memory.updateDelete();
    }
}
