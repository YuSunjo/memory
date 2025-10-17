package com.memory.service.memory

import com.memory.domain.file.repository.FileRepository
import com.memory.domain.map.repository.MapRepository
import com.memory.domain.member.repository.MemberRepository
import com.memory.domain.memory.Memory
import com.memory.domain.memory.MemoryType
import com.memory.domain.memory.repository.MemoryRepository
import com.memory.domain.relationship.Relationship
import com.memory.domain.relationship.RelationshipStatus
import com.memory.domain.relationship.repository.RelationshipRepository
import com.memory.dto.memory.MemoryRequest
import com.memory.dto.memory.response.MemoryResponse
import com.memory.dto.memory.response.MemoryResponse.Companion.from
import com.memory.exception.customException.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class MemoryService(
    private val memoryRepository: MemoryRepository,
    private val memberRepository: MemberRepository,
    private val mapRepository: MapRepository,
    private val fileRepository: FileRepository,
    private val relationshipRepository: RelationshipRepository,
) {
    @Transactional
    fun createMemory(memberId: Long?, createRequest: MemoryRequest.Create): MemoryResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val map = mapRepository.findById(createRequest.mapId)
            .orElseThrow { NotFoundException("지도를 찾을 수 없습니다.") }

        val savedMemory = memoryRepository.save<Memory>(createRequest.toEntity(member, map))

        if (!createRequest.fileIdList.isEmpty()) {
            val files = fileRepository.findAllById(createRequest.fileIdList)
            savedMemory.addFiles(files)
        }

        return from(savedMemory)
    }

    @Transactional(readOnly = true)
    fun findMemoryById(memberId: Long?, memoryId: Long?): MemoryResponse {
        memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val memory = memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)
            .orElseThrow { NotFoundException("해당 유저의 메모리를 찾을 수 없습니다.") }

        return from(memory)
    }

    @Transactional(readOnly = true)
    fun findPublicMemoryById(memoryId: Long): MemoryResponse {
        val memory = memoryRepository.findById(memoryId)
            .orElseThrow { NotFoundException("메모리를 찾을 수 없습니다.") }

        if (!memory.isPublic()) {
            throw NotFoundException("해당 메모리는 공개되지 않았습니다.")
        }

        return from(memory)
    }

    @Transactional(readOnly = true)
    fun findMemoriesByMember(
        memberId: Long?,
        lastMemoryId: Long?,
        size: Int,
        memoryType: MemoryType?
    ): MutableList<MemoryResponse?> {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val relationshipList: List<Relationship> =
            relationshipRepository.findByMemberAndRelationshipStatus(member, RelationshipStatus.ACCEPTED)

        val relatedMemberIds = relationshipList.stream()
            .map<Long> { relationship: Relationship -> relationship.relatedMember.id }
            .toList()

        val memories: List<Memory>?

        if (lastMemoryId == null) {
            memories = memoryRepository.findByMemberAndMemoryType(member, relatedMemberIds, memoryType, size)
        } else {
            memories =
                memoryRepository.findByMemberAndMemoryType(member, relatedMemberIds, memoryType, lastMemoryId, size)
        }

        return memories.stream()
            .map { obj: Memory -> from(obj) }
            .collect(Collectors.toList())
    }

    @Transactional
    fun updateMemory(memberId: Long?, memoryId: Long?, updateRequest: MemoryRequest.Update): MemoryResponse {
        memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val memory = memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)
            .orElseThrow { NotFoundException("해당 유저의 메모리를 찾을 수 없습니다.") }

        // Check if the memory belongs to the member
        if (memory.member.id != memberId) {
            throw NotFoundException("해당 메모리에 접근 권한이 없습니다.")
        }

        memory.update(
            updateRequest.title,
            updateRequest.content,
            updateRequest.locationName,
            updateRequest.memorableDate,
            updateRequest.memoryType
        )

        // Associate files with the memory if fileIdList is not empty
        if (!updateRequest.fileIdList.isEmpty()) {
            val files = fileRepository.findAllById(updateRequest.fileIdList)
            for (file in files) {
                memory.addFile(file)
            }
        }

        return from(memory)
    }

    @Transactional
    fun deleteMemory(memberId: Long?, memoryId: Long?) {
        memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val memory = memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)
            .orElseThrow { NotFoundException("해당 유저의 메모리를 찾을 수 없습니다.") }

        if (memory.member.id != memberId) {
            throw NotFoundException("해당 메모리에 접근 권한이 없습니다.")
        }

        memory.updateDelete()
    }

    @Transactional(readOnly = true)
    fun findPublicMemories(lastMemoryId: Long?, size: Int): MutableList<MemoryResponse?> {
        val memories = if (lastMemoryId == null) {
            memoryRepository.findByMemoryType(MemoryType.PUBLIC, size)
        } else {
            memoryRepository.findByMemoryType(MemoryType.PUBLIC, lastMemoryId, size)
        }

        return memories.stream()
            .map { obj: Memory -> from(obj) }
            .collect(Collectors.toList())
    }

    @Transactional(readOnly = true)
    fun findMemoryEntityById(memberId: Long?, memoryId: Long?): Memory {
        return memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)
            .orElseThrow { NotFoundException("해당 유저의 메모리를 찾을 수 없습니다.") }
    }
}
