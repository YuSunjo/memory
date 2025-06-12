# Memory Domain Module

## Overview
The Memory Domain module contains the core domain models, repositories, and Data Transfer Objects (DTOs) of the application. It defines the business entities and provides data access functionality through repositories.

## Package Structure
```
memory-domain/
├── src/main/java/com/memory/
│   ├── config/                # Domain-specific configurations
│   ├── domain/                # Domain entities
│   │   ├── BaseTimeEntity.java # Base entity with auditing fields
│   │   ├── file/              # File-related entities
│   │   ├── map/               # Map-related entities
│   │   ├── member/            # Member-related entities
│   │   ├── memory/            # Memory-related entities
│   │   └── relationship/      # Relationship-related entities
│   │   └── ...                # Other domain entities
│   └── dto/                   # Data Transfer Objects
│       ├── file/              # File-related DTOs
│       ├── map/               # Map-related DTOs
│       ├── member/            # Member-related DTOs
│       ├── memory/            # Memory-related DTOs
│       └── relationship/      # Relationship-related DTOs
│       └── ...                # Other DTOs
└── src/test/                  # Test classes
```

## Database Configuration
The domain module includes configuration for connecting to the PostgreSQL database with PostGIS support. The database schema is managed through JPA entity mappings.
