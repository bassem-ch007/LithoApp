# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -q

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S litho && adduser -S litho -G litho
COPY --from=build --chown=litho:litho /workspace/target/*.jar app.jar

USER litho
EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
