-- 게임 점수 계산 방식 변경

-- 1. FRIEND_MEMORIES → MEMORIES_RANDOM 이름 변경
UPDATE game_setting
SET game_mode = 'MEMORIES_RANDOM'
WHERE game_mode = 'FRIEND_MEMORIES';

-- 2. 점수 계산 공식 및 기준 거리 업데이트
UPDATE game_setting
SET
    max_distance_for_full_score_km = 100,
    scoring_formula = '1000 - floor((distance_km - 100) / 10)'
WHERE game_mode IN ('MY_MEMORIES', 'MEMORIES_RANDOM', 'RANDOM');