-- 1. 멤버 테이블
CREATE TABLE member (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    member_type VARCHAR(50) NOT NULL DEFAULT 'MEMBER',
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP
);

-- 2. 관계 테이블
CREATE TABLE relationship (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    related_member_id BIGINT NOT NULL,
    relationship_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (related_member_id) REFERENCES member(id)
);

-- 3. 지도 테이블
CREATE TABLE map (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    address VARCHAR(500),
    location GEOMETRY(Point, 4326) NOT NULL,
    map_type VARCHAR(50) NOT NULL,
    member_id BIGINT NOT NULL,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 4. 메모리 테이블
CREATE TABLE memory (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    location_name VARCHAR(255),
    memory_type VARCHAR(50) NOT NULL,
    member_id BIGINT NOT NULL,
    map_id BIGINT,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (map_id) REFERENCES map(id)
);

-- 5. 파일 테이블
CREATE TABLE file (
    id BIGSERIAL PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    memory_id BIGINT,
    member_id BIGINT,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    FOREIGN KEY (memory_id) REFERENCES memory(id),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 6. 멤버 링크 테이블
CREATE TABLE member_link (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    url VARCHAR(500) NOT NULL,
    description VARCHAR(200),
    display_order INTEGER NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_visible BOOLEAN NOT NULL DEFAULT TRUE,
    icon_url VARCHAR(500),
    click_count BIGINT NOT NULL DEFAULT 0,
    last_clicked_at TIMESTAMP,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 7. 할일 테이블
CREATE TABLE todo (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    due_date TIMESTAMP,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    member_id BIGINT NOT NULL,
    repeat_type VARCHAR(50),
    interval INTEGER,
    end_date DATE,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 8. 일기 테이블
CREATE TABLE diary (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    date DATE NOT NULL,
    mood VARCHAR(100),
    weather VARCHAR(100),
    member_id BIGINT NOT NULL,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 9. 캘린더 이벤트 베이스 테이블
CREATE TABLE base_calendar_event (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    location VARCHAR(255),
    member_id BIGINT NOT NULL,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 10. 개인 일정 테이블
CREATE TABLE personal_event (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES base_calendar_event(id)
);

-- 11. 기념일 테이블
CREATE TABLE anniversary_event (
    id BIGINT PRIMARY KEY,
    relationship_id BIGINT,
    is_dday BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id) REFERENCES base_calendar_event(id),
    FOREIGN KEY (relationship_id) REFERENCES relationship(id)
);

-- 12. 관계 일정 테이블
CREATE TABLE relationship_event (
    id BIGINT PRIMARY KEY,
    relationship_id BIGINT,
    FOREIGN KEY (id) REFERENCES base_calendar_event(id),
    FOREIGN KEY (relationship_id) REFERENCES relationship(id)
);

-- 인덱스 생성
CREATE INDEX idx_map_location ON map USING GIST(location);
