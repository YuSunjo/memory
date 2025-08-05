package com.memory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.memory.domain",
    repositoryImplementationPostfix = "CustomImpl",
    repositoryBaseClass = SimpleJpaRepository.class
)
@EnableElasticsearchRepositories(
    basePackages = "com.memory.document",
    repositoryImplementationPostfix = "CustomImpl"
)
public class JpaConfig {
}