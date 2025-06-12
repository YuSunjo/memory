# Memory Infrastructure Module

## Overview
The Memory Infrastructure module provides infrastructure configuration for the application, particularly Docker setup for different environments (local, development, and production). It includes Docker Compose files, Dockerfiles, and Gradle tasks for managing Docker environments.

## Directory Structure
```
memory-infra/
├── docker/                    # Docker configurations
│   ├── local/                 # Local environment setup
│   │   ├── docker-compose.yml # Docker Compose for local environment
│   │   └── ...                # Other local environment files
│   ├── dev/                   # Development environment setup
│   │   ├── docker-compose.yml # Docker Compose for development environment
│   │   └── ...                # Other development environment files
│   └── prod/                  # Production environment setup
│       ├── docker-compose.yml # Docker Compose for production environment
│       └── ...                # Other production environment files
└── src/                       # Source code (if any)
```

## Docker Environments

### Local Environment
The local environment is intended for local development and testing. It includes:

- PostgreSQL database with PostGIS extension
- MinIO (S3-compatible object storage)
- Other necessary services for local development

To start the local environment:
```bash
./gradlew memory-infra:localStart
```

To stop the local environment:
```bash
./gradlew memory-infra:localStop
```

### Development Environment
The development environment is intended for shared development and testing. It includes:

- PostgreSQL database with PostGIS extension
- MinIO (S3-compatible object storage)
- Other necessary services for development

To start the development environment:
```bash
./gradlew memory-infra:devStart
```

To stop the development environment:
```bash
./gradlew memory-infra:devStop
```

### Production Environment
The production environment is intended for production deployment. It includes:

- PostgreSQL database with PostGIS extension
- AWS S3 for object storage
- Other necessary services for production

To start the production environment:
```bash
./gradlew memory-infra:prodStart
```

To stop the production environment:
```bash
./gradlew memory-infra:prodStop
```

## Gradle Tasks
The memory-infra module provides the following Gradle tasks:

- **localStart**: Start the local Docker environment
- **localStop**: Stop the local Docker environment
- **devStart**: Start the development Docker environment
- **devStop**: Stop the development Docker environment
- **prodStart**: Start the production Docker environment
- **prodStop**: Stop the production Docker environment
- **cleanDocker**: Clean up Docker resources

## Dependencies
The memory-infra module has minimal dependencies as it primarily consists of Docker configuration files and Gradle tasks.
