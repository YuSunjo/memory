package com.memory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDateTime;

public class ElasticSearchTC extends ElasticsearchContainer {
    private static final Logger log = LoggerFactory.getLogger(ElasticSearchTC.class);
    private static final String TAGGED_IMAGE = "es-nori:8.11.0";
    private static final String UPSTREAM = "docker.elastic.co/elasticsearch/elasticsearch";
    private static ElasticSearchTC INSTANCE;

    private static DockerImageName imageName() {
        // 1) Dockerfile을 즉석 빌드(최초 1회). 태그: es-nori:8.11.0
        new ImageFromDockerfile(TAGGED_IMAGE, false)
                .withDockerfileFromBuilder(b -> b
                        .from("docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
                        .run("bin/elasticsearch-plugin install --batch analysis-nori")
                        .build()
                )
                .get();

        // 2) 커스텀 이미지를 '엘라스틱 공식 이미지와 호환'이라고 선언
        return DockerImageName.parse(TAGGED_IMAGE)
                .asCompatibleSubstituteFor(UPSTREAM);
    }

    private ElasticSearchTC() {
        super(imageName());
        log.info("[{}] ElasticSearchTC 생성자 호출 - 컨테이너 설정 시작", LocalDateTime.now());

        withEnv("discovery.type", "single-node");
        withEnv("xpack.security.enabled", "false");          // 8.x 필수(비인증 테스트용)
        withEnv("xpack.ml.enabled", "false");                // 메모리 절약
        withEnv("ingest.geoip.downloader.enabled", "false"); // 외부 네트워크/시간 절약
        withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");        // 힙 축소 (필요시 256/512 조정)

        waitingFor(Wait.forHttp("/").forPort(9200).forStatusCode(200));
        withStartupTimeout(Duration.ofMinutes(3))
        .withReuse(false);
    }
    
    public static synchronized ElasticSearchTC getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ElasticSearchTC();
            INSTANCE.start();
        }
        return INSTANCE;
    }
}