package com.memory.service.diary;

import com.memory.domain.diary.Diary;
import com.memory.domain.diary.repository.DiaryRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.diary.DiaryRequest;
import com.memory.dto.diary.response.DiaryResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public DiaryResponse createDiary(Long memberId, DiaryRequest.Create request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Diary diary = request.toEntity(member);

        Diary savedDiary = diaryRepository.save(diary);
        return DiaryResponse.from(savedDiary);
    }

    @Transactional
    public DiaryResponse updateDiary(Long memberId, Long diaryId, DiaryRequest.Update request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundException("다이어리를 찾을 수 없습니다."));

        if (diary.isOwner(member)) {
            throw new ValidationException("해당 다이어리에 대한 권한이 없습니다.");
        }

        diary.update(
                request.getTitle(),
                request.getContent(),
                request.getDate(),
                request.getMood(),
                request.getWeather()
        );

        return DiaryResponse.from(diary);
    }

    @Transactional
    public void deleteDiary(Long memberId, Long diaryId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundException("다이어리를 찾을 수 없습니다."));

        if (diary.isOwner(member)) {
            throw new ValidationException("해당 다이어리에 대한 권한이 없습니다.");
        }

        diary.updateDelete();
    }

    @Transactional(readOnly = true)
    public List<DiaryResponse> getDiariesByDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<Diary> diaries = diaryRepository.findActiveDiariesByMemberAndDateBetween(member, startDate, endDate);
        return diaries.stream()
                .map(DiaryResponse::from)
                .collect(Collectors.toList());
    }
}