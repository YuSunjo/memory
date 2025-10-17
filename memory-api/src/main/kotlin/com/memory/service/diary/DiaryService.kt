package com.memory.service.diary

import com.memory.domain.diary.Diary
import com.memory.domain.diary.repository.DiaryRepository
import com.memory.domain.member.repository.MemberRepository
import com.memory.dto.diary.DiaryRequest
import com.memory.dto.diary.response.DiaryResponse
import com.memory.exception.customException.NotFoundException
import com.memory.exception.customException.ValidationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.stream.Collectors

@Service
class DiaryService(
    private val memberRepository: MemberRepository,
    private val diaryRepository: DiaryRepository,
) {
    @Transactional
    fun createDiary(memberId: Long?, request: DiaryRequest.Create): DiaryResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow{ NotFoundException("회원을 찾을 수 없습니다.") }

        val diary = request.toEntity(member)

        val savedDiary = diaryRepository.save<Diary>(diary)
        return DiaryResponse.Companion.from(savedDiary)
    }

    @Transactional
    fun updateDiary(memberId: Long?, diaryId: Long, request: DiaryRequest.Update): DiaryResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow{ NotFoundException("회원을 찾을 수 없습니다.") }

        val diary = diaryRepository.findById(diaryId)
            .orElseThrow{ NotFoundException("다이어리를 찾을 수 없습니다.") }

        if (diary.isOwner(member)) {
            throw ValidationException("해당 다이어리에 대한 권한이 없습니다.")
        }

        diary.update(
            request.title,
            request.content!!,
            request.date,
            request.mood,
            request.weather
        )

        return DiaryResponse.Companion.from(diary)
    }

    @Transactional
    fun deleteDiary(memberId: Long?, diaryId: Long) {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow{ NotFoundException("회원을 찾을 수 없습니다.") }

        val diary = diaryRepository.findById(diaryId)
            .orElseThrow{ NotFoundException("다이어리를 찾을 수 없습니다.") }

        if (diary.isOwner(member)) {
            throw ValidationException("해당 다이어리에 대한 권한이 없습니다.")
        }

        diary.updateDelete()
    }

    @Transactional(readOnly = true)
    fun getDiariesByDateRange(
        memberId: Long?,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): MutableList<DiaryResponse?> {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow{ NotFoundException("회원을 찾을 수 없습니다.") }

        val diaries: List<Diary> =
            diaryRepository.findActiveDiariesByMemberAndDateBetween(member, startDate, endDate)
        return diaries.stream().map { diary: Diary -> DiaryResponse.Companion.from(diary) }
            .collect(Collectors.toList())
    }
}