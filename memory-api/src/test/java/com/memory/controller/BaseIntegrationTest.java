package com.memory.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {
    static final PostgresTC PG = PostgresTC.getInstance();
    // ElasticSearch 임시 비활성화
    static final ElasticSearchTC ES = ElasticSearchTC.getInstance();

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        // PostgreSQL 설정
        r.add("spring.datasource.url", PG::getJdbcUrl);
        r.add("spring.datasource.username", PG::getUsername);
        r.add("spring.datasource.password", PG::getPassword);
        r.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        
        // ElasticSearch 설정 임시 비활성화
         r.add("spring.elasticsearch.uris", ES::getHttpHostAddress);
    }
}