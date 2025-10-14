plugins {
    id("java")
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

dependencies {
    // Swagger UI
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // Kotlin support
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java", "src/main/kotlin")
        }
    }
}

tasks.register("prepareKotlinBuildScriptModel") {}
