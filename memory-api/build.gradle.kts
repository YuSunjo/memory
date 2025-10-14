plugins {
    id("java")
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = false
}

dependencies {
    implementation(project(":memory-common"))
    implementation(project(":memory-domain"))
    implementation(project(":memory-adapter"))

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Logstash 연동을 위한 JSON 로깅
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:elasticsearch:1.19.3")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    maxParallelForks = 1
}

tasks.register("prepareKotlinBuildScriptModel") {}
