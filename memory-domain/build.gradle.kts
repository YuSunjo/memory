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
    implementation(project(":memory-common"))

    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.data:spring-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // Flyway for database migration
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // PostGIS support
    api("org.hibernate:hibernate-spatial:6.4.0.Final")
    api("org.locationtech.jts:jts-core:1.19.0")

    // QueryDSL
    api("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // ElasticSearch
    api("org.springframework.boot:spring-boot-starter-data-elasticsearch")
}

// QueryDSL 설정
val querydslDir = layout.buildDirectory.dir("generated/querydsl").get().asFile

sourceSets {
    main {
        java {
            srcDirs(querydslDir)
        }
    }
}

tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(file(querydslDir))
}

tasks.clean {
    doLast {
        file(querydslDir).deleteRecursively()
    }
}

tasks.register("prepareKotlinBuildScriptModel") {}