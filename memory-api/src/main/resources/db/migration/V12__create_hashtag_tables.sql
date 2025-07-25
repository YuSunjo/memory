-- Create hash_tag table
CREATE TABLE hash_tag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    use_count BIGINT NOT NULL DEFAULT 0,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP
);

-- Add comments for hash_tag table
COMMENT ON TABLE hash_tag IS '해시태그 테이블';
COMMENT ON COLUMN hash_tag.id IS '해시태그 ID (Primary Key)';
COMMENT ON COLUMN hash_tag.name IS '해시태그 이름 (유니크)';
COMMENT ON COLUMN hash_tag.use_count IS '해시태그 사용 횟수';
COMMENT ON COLUMN hash_tag.create_date IS '생성 일시';
COMMENT ON COLUMN hash_tag.update_date IS '수정 일시';
COMMENT ON COLUMN hash_tag.delete_date IS '삭제 일시 (Soft Delete)';

-- Create memory_hash_tag table (junction table)
CREATE TABLE memory_hash_tag (
    id BIGSERIAL PRIMARY KEY,
    memory_id BIGINT NOT NULL,
    hash_tag_id BIGINT NOT NULL,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    
    CONSTRAINT fk_memory_hash_tag_memory FOREIGN KEY (memory_id) REFERENCES memory(id) ON DELETE CASCADE,
    CONSTRAINT fk_memory_hash_tag_hash_tag FOREIGN KEY (hash_tag_id) REFERENCES hash_tag(id) ON DELETE CASCADE
);

-- Add comments for memory_hash_tag table
COMMENT ON TABLE memory_hash_tag IS '메모리-해시태그 연결 테이블';
COMMENT ON COLUMN memory_hash_tag.id IS '연결 ID (Primary Key)';
COMMENT ON COLUMN memory_hash_tag.memory_id IS '메모리 ID (Foreign Key)';
COMMENT ON COLUMN memory_hash_tag.hash_tag_id IS '해시태그 ID (Foreign Key)';
COMMENT ON COLUMN memory_hash_tag.create_date IS '생성 일시';
COMMENT ON COLUMN memory_hash_tag.update_date IS '수정 일시';
COMMENT ON COLUMN memory_hash_tag.delete_date IS '삭제 일시 (Soft Delete)';

