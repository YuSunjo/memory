plugins {
    id("base")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = false
}

// Docker Compose tasks for local environment
tasks.register<Exec>("localStart") {
    description = "Start local Docker environment"
    group = "docker"
    workingDir = file("docker/local")
    commandLine("docker-compose", "up", "-d")
    doLast {
        println("Local Docker environment started")
    }
}

tasks.register<Exec>("localStop") {
    description = "Stop local Docker environment"
    group = "docker"
    workingDir = file("docker/local")
    commandLine("docker-compose", "down")
    doLast {
        println("Local Docker environment stopped")
    }
}

// Docker Compose tasks for dev environment
tasks.register<Exec>("devStart") {
    description = "Start dev Docker environment"
    group = "docker"
    workingDir = file("docker/dev")
    commandLine("docker-compose", "up", "-d")
    doLast {
        println("Dev Docker environment started")
    }
}

tasks.register<Exec>("devStop") {
    description = "Stop dev Docker environment"
    group = "docker"
    workingDir = file("docker/dev")
    commandLine("docker-compose", "down")
    doLast {
        println("Dev Docker environment stopped")
    }
}

// Docker Compose tasks for prod environment
tasks.register<Exec>("prodStart") {
    description = "Start prod Docker environment"
    group = "docker"
    workingDir = file("docker/prod")
    commandLine("docker-compose", "up", "-d")
    doLast {
        println("Prod Docker environment started")
    }
}

tasks.register<Exec>("prodStop") {
    description = "Stop prod Docker environment"
    group = "docker"
    workingDir = file("docker/prod")
    commandLine("docker-compose", "down")
    doLast {
        println("Prod Docker environment stopped")
    }
}

// Docker cleanup task
tasks.register<Exec>("cleanDocker") {
    group = "docker"
    description = "Docker 리소스를 정리합니다"
    commandLine("docker", "system", "prune", "-f")
}

tasks.register("prepareKotlinBuildScriptModel") {}