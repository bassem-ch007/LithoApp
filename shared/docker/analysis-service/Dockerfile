# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace
COPY services/analysis-service/pom.xml ./pom.xml
RUN mvn -B dependency:go-offline

COPY services/analysis-service/src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build --chown=app:app /workspace/target/*.jar /app/app.jar

USER app
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
