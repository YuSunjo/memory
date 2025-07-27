package com.memory.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class OneTimePoolReset {

    private final ConnectionPoolManager connectionPoolManager;
    private final AtomicBoolean hasExecuted = new AtomicBoolean(false);

    // ApplicationReadyEvent 제거 - JWT 예외시에만 실행

    /**
     * 첫 번째 JWT 예외 발생시에만 실행되는 안전한 풀 리셋
     * 이후 JWT 예외에서는 무시됨
     */
    public void executeOnFirstJwtException() {
        if (hasExecuted.compareAndSet(false, true)) {
            try {
                log.warn("🔥 첫 번째 JWT 예외 발생 - 연결 풀 리셋 실행");
                connectionPoolManager.forcePoolReset();
                log.warn("✅ 첫 JWT 예외 연결 풀 리셋 완료");
            } catch (Exception e) {
                log.error("❌ JWT 예외 연결 풀 리셋 실패", e);
            }
        } else {
            log.debug("⏭️ 이미 첫 JWT 예외 처리됨 - 풀 리셋 스킵");
        }
    }

    /**
     * 실행 상태 확인
     */
    public boolean hasBeenExecuted() {
        return hasExecuted.get();
    }
}