package com.memory.domain.diary.repository

import com.memory.domain.diary.Diary
import org.springframework.data.jpa.repository.JpaRepository

interface DiaryRepository : JpaRepository<Diary, Long>, DiaryRepositoryCustom
