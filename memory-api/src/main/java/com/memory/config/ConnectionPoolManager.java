package com.memory.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionPoolManager {

    private final DataSource dataSource;

    public void forcePoolReset() {
        try {
            if (dataSource instanceof HikariDataSource) {
                HikariDataSource hikariDataSource = (HikariDataSource) dataSource;

                log.warn("🔄 강제 연결 풀 리셋 시작");

                // 현재 연결 상태 로그
                log.info("리셋 전 - Active: {}, Idle: {}, Total: {}",
                    hikariDataSource.getHikariPoolMXBean().getActiveConnections(),
                    hikariDataSource.getHikariPoolMXBean().getIdleConnections(),
                    hikariDataSource.getHikariPoolMXBean().getTotalConnections());

                // 유휴 연결 제거
                hikariDataSource.getHikariPoolMXBean().softEvictConnections();

                // 잠시 대기
                Thread.sleep(100);

                log.info("리셋 후 - Active: {}, Idle: {}, Total: {}",
                    hikariDataSource.getHikariPoolMXBean().getActiveConnections(),
                    hikariDataSource.getHikariPoolMXBean().getIdleConnections(),
                    hikariDataSource.getHikariPoolMXBean().getTotalConnections());

                log.warn("✅ 강제 연결 풀 리셋 완료");
            }
        } catch (Exception e) {
            log.error("연결 풀 리셋 실패", e);
        }
    }
}