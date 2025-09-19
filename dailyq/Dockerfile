# Multi-stage build for Spring Boot application
FROM gradle:8.11-jdk21 AS build

# Set working directory
WORKDIR /app

# Copy gradle wrapper and build files
COPY gradle/ gradle/
COPY gradlew gradlew.bat build.gradle settings.gradle ./

# Download dependencies first (this layer will be cached)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src/ src/

# Build the application with optimized settings
ENV GRADLE_OPTS="-Xmx1024m -Xms512m -XX:+UseG1GC -Dorg.gradle.daemon=false -Dorg.gradle.parallel=false"
RUN ./gradlew assemble -x test --no-daemon --stacktrace

# Runtime stage
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -r dailyq && useradd -r -g dailyq dailyq

# Copy the built JAR from build stage
# Copy only Spring Boot fat JAR (exclude plain JAR)
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# Change ownership to non-root user
RUN chown -R dailyq:dailyq /app
USER dailyq

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]