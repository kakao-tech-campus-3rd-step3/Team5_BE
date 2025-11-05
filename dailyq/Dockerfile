# Multi-stage build for Spring Boot application
FROM gradle:8.11-jdk21 AS build

# Set working directory
WORKDIR /app

# Gradle Cache
ENV GRADLE_USER_HOME=/app/.gradle

# Copy gradle wrapper and build files
COPY gradle/ gradle/
COPY gradlew gradlew.bat build.gradle settings.gradle ./

# Download dependencies first (this layer will be cached)
RUN ./gradlew dependencies --no-daemon --stacktrace

# Copy source code
COPY src/ src/

# Build the application with optimized settings
RUN ./gradlew bootJar --no-daemon --stacktrace

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
HEALTHCHECK --interval=15s --timeout=10s --start-period=120s --retries=10 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with optimized JVM settings
ENTRYPOINT ["java", \
    "-Xms256m", "-Xmx512m", \
    "-XX:+UseG1GC", \
    "-XX:+UseStringDeduplication", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]