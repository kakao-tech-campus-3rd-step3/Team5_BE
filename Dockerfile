# 1) Build stage
FROM gradle:8.7-jdk21-alpine AS build
WORKDIR /workspace
COPY . .
RUN gradle clean bootJar --no-daemon

# 2) Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=build /workspace/dailyq/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]