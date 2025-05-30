# Memory Infrastructure Docker Setup

This directory contains Docker configurations for different environments (local, dev, prod) for the Memory application.

## Directory Structure

```
docker/
├── Dockerfile                # Main Dockerfile for the application
├── local/                    # Local environment configuration
│   └── docker-compose.yml    # Docker Compose for local development
├── dev/                      # Development environment configuration
│   ├── .env                  # Environment variables for dev
│   └── docker-compose.yml    # Docker Compose for development
└── prod/                     # Production environment configuration
    ├── .env                  # Environment variables for prod
    └── docker-compose.yml    # Docker Compose for production
```

## Usage

You can use either Docker Compose commands directly or Gradle tasks to manage the Docker environments.

### Using Gradle Tasks

The project includes Gradle tasks to start and stop Docker environments:

```bash
# Start local environment
./gradlew :memory-infra:localStart

# Stop local environment
./gradlew :memory-infra:localStop

# Start development environment
./gradlew :memory-infra:devStart

# Stop development environment
./gradlew :memory-infra:devStop

# Start production environment
./gradlew :memory-infra:prodStart

# Stop production environment
./gradlew :memory-infra:prodStop
```

### Using Docker Compose Directly

Alternatively, you can use Docker Compose commands directly:

#### Local Environment

For local development, you can start the services (PostgreSQL with PostGIS and Redis) without building the application:

```bash
cd memory-infra/docker/local
docker-compose up -d
```

#### Development Environment

For the development environment, which includes the application, PostgreSQL with PostGIS, and Redis:

```bash
cd memory-infra/docker/dev
docker-compose up -d
```

#### Production Environment

For the production environment:

```bash
cd memory-infra/docker/prod
docker-compose up -d
```

## PostgreSQL and PostGIS

This project uses PostgreSQL with the PostGIS extension for spatial data handling. PostGIS adds support for geographic objects to the PostgreSQL database, allowing for location queries to be run in SQL.

### Key Features of PostGIS
- Store geographic data in a PostgreSQL database
- Perform spatial queries using SQL
- Support for geographic objects like points, lines, polygons
- Spatial indexing for improved query performance

## Environment Variables

The `.env` files in the dev and prod directories contain environment variables used by the Docker Compose files. You should update these files with secure passwords before deploying to actual development or production environments.

## Customization

You can customize the Docker Compose files and environment variables according to your specific requirements.
