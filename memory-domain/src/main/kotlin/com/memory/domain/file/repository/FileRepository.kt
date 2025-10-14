package com.memory.domain.file.repository

import com.memory.domain.file.File
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<File, Long>, FileRepositoryCustom
