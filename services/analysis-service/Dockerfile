# syntax=docker/dockerfile:1
# =============================================================================
# LithoApp — analysis-service
# Java 17 | Spring Boot 3.x
# Build:  docker build -t lithoapp/analysis-service .
# =============================================================================

# ---- Stage 1: build ----
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /build

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -q

COPY src src
RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests -q

# ---- Stage 2: runtime ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S litho && adduser -S litho -G litho

COPY --from=builder --chown=litho:litho /build/target/*.jar app.jar

USER litho

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
