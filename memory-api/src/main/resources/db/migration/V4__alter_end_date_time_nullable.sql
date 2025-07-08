-- base_calendar_event 테이블의 end_date_time 컬럼을 nullable로 변경
ALTER TABLE base_calendar_event
    ALTER COLUMN end_date_time DROP NOT NULL;