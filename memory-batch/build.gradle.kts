plugins {
    id("java")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

dependencies {
    implementation(project(":memory-domain"))

    implementation("org.springframework.boot:spring-boot-starter-batch")
    testImplementation("org.springframework.batch:spring-batch-test")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.register("prepareKotlinBuildScriptModel") {}