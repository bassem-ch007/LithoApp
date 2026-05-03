# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace/services/analysis-service

COPY services/analysis-service/pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -q

COPY services/analysis-service/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S litho && adduser -S litho -G litho
RUN mkdir -p /app/uploads && chown -R litho:litho /app
COPY --from=build --chown=litho:litho /workspace/services/analysis-service/target/*.jar app.jar

USER litho
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
