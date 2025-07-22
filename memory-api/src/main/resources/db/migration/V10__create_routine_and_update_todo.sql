-- V10: Routine 테이블 생성, Todo 테이블 수정 및 기존 repeat 컬럼 정리

-- 1. Routine 테이블 생성
CREATE TABLE routine (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    member_id BIGINT NOT NULL,
    repeat_type VARCHAR(50) NOT NULL DEFAULT 'NONE',
    interval INTEGER,
    start_date DATE,
    end_date DATE,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 2. Todo 테이블에 새로운 컬럼 추가
ALTER TABLE todo 
ADD COLUMN is_routine BOOLEAN NULL DEFAULT FALSE,
ADD COLUMN routine_id BIGINT;

-- 3. Todo 테이블에 외래키 제약조건 추가
ALTER TABLE todo 
ADD CONSTRAINT fk_todo_routine 
FOREIGN KEY (routine_id) REFERENCES routine(id);

-- 8. 기존 repeat 관련 컬럼 제거
ALTER TABLE todo DROP COLUMN IF EXISTS repeat_type;
ALTER TABLE todo DROP COLUMN IF EXISTS interval;
ALTER TABLE todo DROP COLUMN IF EXISTS end_date;

-- 테이블 코멘트
COMMENT ON TABLE routine IS '사용자 루틴 정보를 저장하는 테이블';
COMMENT ON COLUMN routine.active IS '루틴 활성화 여부';
COMMENT ON COLUMN routine.repeat_type IS '반복 유형 (DAILY, WEEKLY, MONTHLY, YEARLY)';
COMMENT ON COLUMN routine.interval IS '반복 간격 (예: 2주마다 = 2)';
COMMENT ON COLUMN routine.start_date IS '반복 시작일';
COMMENT ON COLUMN routine.end_date IS '반복 종료일 (NULL이면 무한반복)';

COMMENT ON COLUMN todo.is_routine IS '루틴에서 생성된 할일인지 여부';
COMMENT ON COLUMN todo.routine_id IS '원본 루틴 ID (루틴에서 생성된 경우)';