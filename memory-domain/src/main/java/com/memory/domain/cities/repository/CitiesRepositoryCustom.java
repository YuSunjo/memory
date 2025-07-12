package com.memory.domain.cities.repository;

import com.memory.domain.cities.Cities;

import java.util.Optional;

public interface CitiesRepositoryCustom {

    Optional<Cities> findRandomCities();

}
