# Memory API Module

## Overview
The Memory API module is the entry point of the application, containing the REST API controllers, services, and application configuration. It handles HTTP requests, processes them using the appropriate services, and returns responses to clients.

## Package Structure
```
memory-api/
├── src/main/java/com/memory/
│   ├── MemoryApiApplication.java  # Application entry point
│   ├── config/                    # API-specific configurations
│   │   ├── exception/             # Exception handling
│   │   ├── jwt/                   # JWT configuration
│   │   └── swagger/               # Swagger/OpenAPI configuration
│   ├── controller/                # REST API controllers
│   │   ├── file/                  # File upload/download controllers
│   │   ├── map/                   # Map controllers
│   │   ├── member/                # Member controllers
│   │   ├── memory/                # Memory controllers
│   │   └── relationship/          # Relationship controllers
│   │   └── ...                    # Other controllers
│   └── service/                   # Business logic services
│       ├── file/                  # File services
│       ├── map/                   # Map services
│       ├── member/                # Member services
│       ├── memory/                # Memory services
│       └── relationship/          # Relationship services
│       └── ...                    # Other services
└── src/test/                      # Test classes
```

## Running the API
To run the API module:

```bash
./gradlew memory-api:bootRun
```

The API will be available at `http://localhost:8080` and the Swagger UI documentation will be available at `http://localhost:8080/swagger-ui.html`.
