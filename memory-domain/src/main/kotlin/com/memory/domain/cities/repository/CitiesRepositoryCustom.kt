package com.memory.domain.cities.repository

import com.memory.domain.cities.Cities
import java.util.Optional

interface CitiesRepositoryCustom {

    fun findRandomCities(): Optional<Cities>

}
