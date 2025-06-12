# Memory Project

## Overview
Todo

## Project Structure
The project is organized into multiple modules following a modular architecture:

### Modules
- **memory-api**: Contains the REST API controllers, services, and application entry point
- **memory-domain**: Contains the domain models, repositories, and DTOs
- **memory-common**: Contains common utilities, configurations, and cross-cutting concerns
- **memory-infra**: Contains infrastructure configuration, particularly Docker setup

### Package Structure
```
memory/
├── memory-api/
│   ├── src/main/java/com/memory/
│   │   ├── config/         # API-specific configurations
│   │   ├── controller/     # REST API controllers
│   │   └── service/        # Business logic services
├── memory-domain/
│   ├── src/main/java/com/memory/
│   │   ├── domain/         # Domain entities
│   │   ├── dto/            # Data Transfer Objects
│   │   └── config/         # Domain-specific configurations
├── memory-common/
│   ├── src/main/java/com/memory/
│   │   ├── annotation/     # Custom annotations
│   │   ├── component/      # Common components
│   │   ├── config/         # Common configurations
│   │   ├── dto/            # Common DTOs
│   │   ├── exception/      # Exception handling
│   │   ├── response/       # Response handling
│   │   └── service/        # Common services
└── memory-infra/
    ├── docker/             # Docker configurations
    │   ├── local/          # Local environment setup
    │   ├── dev/            # Development environment setup
    │   └── prod/           # Production environment setup
```

## Technology Stack
- **Java 17**: Programming language
- **Spring Boot 3.5.0**: Application framework
- **Spring Data JPA**: Data access
- **PostgreSQL**: Database
- **PostGIS**: Geospatial data support
- **QueryDSL**: Type-safe queries
- **JWT**: Authentication
- **Swagger/OpenAPI**: API documentation
- **AWS S3**: File storage
- **Docker**: Containerization

## Setup Instructions

### Prerequisites
- Java 17
- Docker and Docker Compose
- Gradle

### Local Development Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/memory.git
   cd memory
   ```

2. Start the local Docker environment:
   ```bash
   ./gradlew memory-infra:localStart
   ```

3. Build the project:
   ```bash
   ./gradlew build
   ```

4. Run the application:
   ```bash
   ./gradlew memory-api:bootRun
   ```

5. Access the API documentation:
   ```
   http://localhost:8080/swagger-ui.html
   ```

### Docker Environments
- **Local**: `./gradlew memory-infra:localStart` and `./gradlew memory-infra:localStop`
- **Development**: `./gradlew memory-infra:devStart` and `./gradlew memory-infra:devStop`
- **Production**: `./gradlew memory-infra:prodStart` and `./gradlew memory-infra:prodStop`

## API Endpoints

The application provides the following main API endpoints:

- **Member API**: User registration, authentication, and profile management
- **Map API**: Creation and management of maps
- **Memory API**: Creation and management of memories associated with maps
- **Relationship API**: Management of relationships between users
- **File API**: Upload and management of files (images)
- ... Other APIs as defined in the controllers

For detailed API documentation, refer to the Swagger UI available at `http://localhost:8080/swagger-ui.html` when the application is running.

## License
