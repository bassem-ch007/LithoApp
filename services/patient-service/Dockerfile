# syntax=docker/dockerfile:1
# =============================================================================
# LithoApp — patient-service
# Java 21 | Spring Boot 3.x
# Build:  docker build -t lithoapp/patient-service .
# =============================================================================

# ---- Stage 1: build ----
# Use the official Maven image — no mvnw needed.
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy POM first so that the dependency layer is cached separately from source.
# If pom.xml hasn't changed, Maven will reuse the cached layer on the next build.
COPY pom.xml .

# Download all declared dependencies (and plugins) into the layer cache.
# --mount=type=cache keeps ~/.m2 across builds without baking it into the image.
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -q

# Now copy source and build the fat JAR.
COPY src src
RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests -q

# ---- Stage 2: runtime ----
# Lean JRE-only image — no compiler, no Maven, no source code.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root system user before copying the jar.
RUN addgroup -S litho && adduser -S litho -G litho

# Copy the fat JAR and immediately assign ownership to the litho user.
COPY --from=builder --chown=litho:litho /build/target/*.jar app.jar

# Switch to non-root for all subsequent instructions (ENTRYPOINT included).
USER litho

EXPOSE 8086

# Exec form — no shell wrapper.
# JVM flags can be injected at runtime via JAVA_TOOL_OPTIONS env var
# (recognised natively by the JVM — no shell expansion required).
# Example: JAVA_TOOL_OPTIONS="-Xms128m -Xmx256m"
ENTRYPOINT ["java", "-jar", "app.jar"]
