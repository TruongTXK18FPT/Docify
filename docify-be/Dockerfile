# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /workspace

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Pandoc is required for Markdown <-> DOCX/PDF conversions.
# Gotenberg handles DOCX/PPTX -> PDF as a separate service.
RUN apt-get update \
    && apt-get install -y --no-install-recommends pandoc ca-certificates curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /workspace/target/docify-be-0.0.1-SNAPSHOT.jar /app/app.jar

RUN mkdir -p /app/storage/tmp /app/storage/results

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod \
    DOCIFY_STORAGE_DIR=/app/storage \
    DOCIFY_TEMP_DIR=/app/storage/tmp \
    DOCIFY_RESULT_DIR=/app/storage/results \
    PANDOC_BINARY=pandoc

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
    CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
