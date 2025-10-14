package com.memory.domain.cities.repository

import com.memory.domain.cities.Cities
import org.springframework.data.jpa.repository.JpaRepository

interface CitiesRepository : JpaRepository<Cities, Long>, CitiesRepositoryCustom
