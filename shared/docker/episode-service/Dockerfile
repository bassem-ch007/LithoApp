# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace
COPY services/episode-service/pom.xml ./pom.xml
RUN mvn -B dependency:go-offline

COPY services/episode-service/src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build --chown=app:app /workspace/target/*.jar /app/app.jar

USER app
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
