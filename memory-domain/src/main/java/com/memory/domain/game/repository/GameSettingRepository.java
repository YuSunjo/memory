package com.memory.domain.game.repository;

import com.memory.domain.game.GameSetting;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GameSettingRepository extends JpaRepository<GameSetting, Long>, GameSettingRepositoryCustom {

}
