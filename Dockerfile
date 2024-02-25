FROM maven:3-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn clean verify --fail-never -DskipTests
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-alpine
WORKDIR /app

COPY --from=build /app/target/spring-boot-javalab-0.0.1-SNAPSHOT.jar spring-boot-javalab.jar

CMD ["java", "-jar", "spring-boot-javalab.jar"]