-- 게임 세션 테이블
CREATE TABLE game_session (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    target_member_id BIGINT,
    game_mode VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS',
    total_score INTEGER NOT NULL DEFAULT 0,
    total_questions INTEGER NOT NULL DEFAULT 0,
    correct_answers INTEGER NOT NULL DEFAULT 0,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (target_member_id) REFERENCES member(id) ON DELETE CASCADE
);

-- 게임 문제 테이블
CREATE TABLE game_question (
    id BIGSERIAL PRIMARY KEY,
    game_session_id BIGINT NOT NULL,
    memory_id BIGINT NOT NULL,
    question_order INTEGER NOT NULL,
    
    -- 정답 위치
    correct_latitude DECIMAL(10, 8) NOT NULL,
    correct_longitude DECIMAL(11, 8) NOT NULL,
    correct_location_name VARCHAR(255),
    
    -- 플레이어 답안
    player_latitude DECIMAL(10, 8),
    player_longitude DECIMAL(11, 8),
    distance_km DECIMAL(8, 2),
    score INTEGER,
    time_taken_seconds INTEGER,
    answered_at TIMESTAMP,
    
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    
    FOREIGN KEY (game_session_id) REFERENCES game_session(id) ON DELETE CASCADE,
    FOREIGN KEY (memory_id) REFERENCES memory(id) ON DELETE CASCADE
);

-- 게임 설정 테이블
CREATE TABLE game_setting (
    id BIGSERIAL PRIMARY KEY,
    game_mode VARCHAR(50) NOT NULL UNIQUE,
    max_questions INTEGER NOT NULL DEFAULT 10,
    time_limit_seconds INTEGER NOT NULL DEFAULT 60,
    max_distance_for_full_score_km INTEGER NOT NULL DEFAULT 1,
    scoring_formula VARCHAR(500) DEFAULT 'MAX(0, 1000 - (distance_km * 100))',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP
);

-- 게임 랭킹 테이블
CREATE TABLE game_ranking (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    game_mode VARCHAR(50) NOT NULL,
    
    -- 개인 최고 기록
    best_score INTEGER NOT NULL DEFAULT 0,
    best_accuracy DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    best_avg_distance_km DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    
    -- 전체 통계
    total_games_played INTEGER NOT NULL DEFAULT 0,
    total_score BIGINT NOT NULL DEFAULT 0,
    total_questions_answered INTEGER NOT NULL DEFAULT 0,
    
    -- 랭킹 점수 (주간/월간 계산용)
    weekly_score INTEGER NOT NULL DEFAULT 0,
    monthly_score INTEGER NOT NULL DEFAULT 0,
    
    last_played_at TIMESTAMP,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    UNIQUE (member_id, game_mode)
);

-- 기본 게임 설정 데이터 삽입
INSERT INTO game_setting (game_mode, max_questions, time_limit_seconds, max_distance_for_full_score_km, scoring_formula) VALUES
('MY_MEMORIES', 5, 60, 1, 'MAX(0, 1000 - (distance_km * 100))'),
('FRIEND_MEMORIES', 5, 60, 1, 'MAX(0, 1000 - (distance_km * 100))'),
('RANDOM', 5, 60, 1, 'MAX(0, 1000 - (distance_km * 100))');

-- 코멘트 추가
COMMENT ON TABLE game_session IS '게임 세션 정보를 저장하는 테이블';
COMMENT ON TABLE game_question IS '게임 내 개별 문제와 답안을 저장하는 테이블';
COMMENT ON TABLE game_setting IS '게임 모드별 설정을 저장하는 테이블';
COMMENT ON TABLE game_ranking IS '사용자별 게임 랭킹과 통계를 저장하는 테이블';

COMMENT ON COLUMN game_session.target_member_id IS 'FRIEND_MEMORIES 모드에서 대상이 되는 친구의 ID';
COMMENT ON COLUMN game_question.distance_km IS '정답 위치와 플레이어 답안 간의 거리(km)';
COMMENT ON COLUMN game_ranking.weekly_score IS '주간 랭킹 계산용 점수 (매주 리셋)';
COMMENT ON COLUMN game_ranking.monthly_score IS '월간 랭킹 계산용 점수 (매월 리셋)';
