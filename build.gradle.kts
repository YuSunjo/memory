
plugins {
    id("java")
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
}

repositories {
    mavenCentral()
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

tasks.register<JacocoReport>("jacocoRootReport") {
    description = "Generates an aggregate report from all subprojects"
    group = "reporting"

    subprojects {
        val subproject = this
        subproject.plugins.withType<JacocoPlugin> {
            dependsOn(subproject.tasks.test)

            sourceDirectories.from(subproject.sourceSets.main.get().allSource.srcDirs)
            classDirectories.from(subproject.sourceSets.main.get().output)
            executionData.from(subproject.tasks.jacocoTestReport.get().executionData)
        }
    }

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }

    doFirst {
        executionData = files(executionData.filter { it.exists() })
    }
}

allprojects {
    group = "com"
    version = "0.0.1-SNAPSHOT"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    repositories {
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-aop")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testCompileOnly("org.projectlombok:lombok")
        testAnnotationProcessor("org.projectlombok:lombok")
    }

    jacoco {
        toolVersion = "0.8.10"
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test, tasks.compileJava, tasks.processResources)
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }

        classDirectories.setFrom(
            files(classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/config/**",
                        "**/dto/**",
                        "**/entity/**",
                        "**/domain/**",
                        "**/*Application*",
                        "**/*Config*",
                        "**/*Exception*"
                    )
                }
            })
        )
    }

    tasks.jacocoTestCoverageVerification {
        dependsOn(tasks.jacocoTestReport)

        violationRules {
            rule {
                limit {
                    minimum = 0.70.toBigDecimal()
                }
            }
        }
    }

    tasks.test {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }
}