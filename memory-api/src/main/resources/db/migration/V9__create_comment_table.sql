-- 댓글 테이블 생성
CREATE TABLE comment (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    depth INTEGER NOT NULL DEFAULT 0,
    memory_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    parent_id BIGINT,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP,
    
    CONSTRAINT fk_comment_memory FOREIGN KEY (memory_id) REFERENCES memory(id),
    CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comment(id)
);

-- 댓글 테이블에 대한 코멘트
COMMENT ON TABLE comment IS '댓글 테이블';
COMMENT ON COLUMN comment.id IS '댓글 ID (PK)';
COMMENT ON COLUMN comment.content IS '댓글 내용';
COMMENT ON COLUMN comment.depth IS '댓글 깊이 (0: 최상위 댓글, 1: 대댓글)';
COMMENT ON COLUMN comment.memory_id IS '메모리 ID (FK)';
COMMENT ON COLUMN comment.member_id IS '작성자 ID (FK)';
COMMENT ON COLUMN comment.parent_id IS '부모 댓글 ID (FK, 대댓글인 경우)';
COMMENT ON COLUMN comment.create_date IS '생성일시';
COMMENT ON COLUMN comment.update_date IS '수정일시';
COMMENT ON COLUMN comment.delete_date IS '삭제일시 (논리 삭제)';
