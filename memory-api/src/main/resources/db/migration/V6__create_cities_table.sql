-- cities 테이블 생성 (PostgreSQL)
CREATE TABLE IF NOT EXISTS cities (
    id BIGSERIAL PRIMARY KEY,
    geoname_id BIGINT,
    name VARCHAR(200),
    asciiname VARCHAR(200),
    country_code VARCHAR(10),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    population BIGINT,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    delete_date TIMESTAMP
);

-- 성능을 위한 인덱스 추가
CREATE INDEX IF NOT EXISTS idx_cities_country_code ON cities(country_code);
CREATE INDEX IF NOT EXISTS idx_cities_name ON cities(name);
CREATE INDEX IF NOT EXISTS idx_cities_geoname_id ON cities(geoname_id);
CREATE INDEX IF NOT EXISTS idx_cities_location ON cities(latitude, longitude);

-- 테이블과 컬럼에 주석 추가
COMMENT ON TABLE cities IS '전 세계 도시 정보 테이블';
COMMENT ON COLUMN cities.id IS '도시 고유 ID';
COMMENT ON COLUMN cities.geoname_id IS 'GeoNames 데이터베이스 ID';
COMMENT ON COLUMN cities.name IS '도시명 (현지 언어)';
COMMENT ON COLUMN cities.asciiname IS '도시명 (ASCII)';
COMMENT ON COLUMN cities.country_code IS '국가 코드 (ISO)';
COMMENT ON COLUMN cities.latitude IS '위도';
COMMENT ON COLUMN cities.longitude IS '경도';
COMMENT ON COLUMN cities.population IS '인구수';
