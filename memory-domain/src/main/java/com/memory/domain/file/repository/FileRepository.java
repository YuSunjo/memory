package com.memory.domain.file.repository;

import com.memory.domain.file.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {
}