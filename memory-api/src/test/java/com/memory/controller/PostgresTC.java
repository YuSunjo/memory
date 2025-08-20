package com.memory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

public class PostgresTC extends PostgreSQLContainer<PostgresTC> {
    private static final Logger log = LoggerFactory.getLogger(PostgresTC.class);
    private static final DockerImageName IMG = DockerImageName.parse("postgis/postgis:15-3.3")
            .asCompatibleSubstituteFor("postgres");
    private static PostgresTC INSTANCE;

    private PostgresTC() {
        super(IMG);
        log.info("[{}] PostgresTC 생성자 호출 - 컨테이너 설정 시작", java.time.LocalDateTime.now());

        withDatabaseName("memory_test");
        withUsername("test");
        withPassword("test");
        withStartupTimeout(Duration.ofMinutes(5));
        waitingFor(Wait.forListeningPort())
        .withReuse(false);
    }
    
    public static synchronized PostgresTC getInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new PostgresTC(); 
            INSTANCE.start(); 
        }
        return INSTANCE;
    }
}