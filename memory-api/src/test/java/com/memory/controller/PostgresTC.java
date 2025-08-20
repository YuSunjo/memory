package com.memory.controller;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresTC extends PostgreSQLContainer<PostgresTC> {
    private static final DockerImageName IMG = DockerImageName.parse("postgis/postgis:15-3.3")
            .asCompatibleSubstituteFor("postgres");
    private static PostgresTC INSTANCE;

    private PostgresTC() {
        super(IMG);
        withDatabaseName("memory_test");
        withUsername("test");
        withPassword("test");
        // 필요하면 .waitingFor(Wait.forListeningPort());
    }
    public static synchronized PostgresTC getInstance() {
        if (INSTANCE == null) { INSTANCE = new PostgresTC(); INSTANCE.start(); }
        return INSTANCE;
    }
}