# Memory Common Module

## Overview
The Memory Common module provides common utilities, configurations, and cross-cutting concerns that are used across the application. It includes custom annotations, exception handling, response handling, and common services.

## Package Structure
```
memory-common/
├── src/main/java/com/memory/
│   ├── annotation/            # Custom annotations
│   │   ├── Auth.java          # Authentication annotation
│   │   ├── MemberId.java      # Member ID annotation
│   │   └── swagger/           # Swagger annotations
│   ├── component/             # Common components
│   │   └── storage/           # Storage components (S3)
│   ├── config/                # Common configurations
│   │   ├── security/          # Security configuration
│   │   └── web/               # Web configuration
│   ├── dto/                   # Common DTOs
│   │   └── UploadResponse.java # Upload response DTO
│   ├── exception/             # Exception handling
│   │   ├── customException/   # Custom exceptions
│   │   ├── errorCode/         # Error codes
│   │   └── handler/           # Exception handlers
│   ├── response/              # Response handling
│   │   └── ServerResponse.java # Standard response format
│   └── service/               # Common services
│       └── upload/            # File upload services
└── src/test/                  # Test classes
```

## Features

### Custom Annotations
The common module provides custom annotations for various purposes:

- **@Auth**: Marks endpoints that require authentication
- **@MemberId**: Injects the member ID from the JWT token
- **@ApiOperations**: Provides Swagger documentation annotations

### Exception Handling
The common module provides a centralized exception handling mechanism:

- **Custom Exceptions**: Specific exceptions for different error scenarios
- **Error Codes**: Standardized error codes for consistent error responses
- **Exception Handlers**: Global exception handlers for converting exceptions to appropriate responses

### Response Handling
The common module provides a standardized response format:

- **ServerResponse**: A generic response wrapper that includes status, data, and error information

### File Upload Services
The common module provides services for file upload and storage:

- **S3Service**: Service for uploading files to AWS S3
- **UploadService**: Service for handling file uploads with validation

### Security Configuration
The common module provides security configuration:

- **JWT Authentication**: Configuration for JWT-based authentication
- **Security Filters**: Security filters for protecting endpoints

## Dependencies
The memory-common module has the following dependencies:

- **JWT**: For authentication
- **Swagger/OpenAPI**: For API documentation
- **AWS S3**: For file storage
- **Spring Security**: For security configuration
