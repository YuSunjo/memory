FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copy the Gradle wrapper files
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .

# Copy the project modules
COPY memory-api memory-api
COPY memory-common memory-common
COPY memory-domain memory-domain
COPY memory-infra memory-infra

# Make the Gradle wrapper executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew :memory-api:clean :memory-api:build -x test

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the built artifact from builder stage
COPY --from=builder /app/memory-api/build/libs/memory-api-0.0.1-SNAPSHOT.jar app.jar

# Set the JAR file as the entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]