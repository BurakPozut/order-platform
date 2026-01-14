# Build Stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Step 1: Build common-lib first
COPY common-lib/pom.xml ./common-lib/
COPY common-lib/src ./common-lib/src
WORKDIR /app/common-lib
RUN mvn clean install -DskipTests

# Step 2: Now build notification-service
WORKDIR /app
COPY notification-service/pom.xml ./notification-service/
WORKDIR /app/notification-service
RUN mvn dependency:go-offline -B
COPY notification-service/src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/notification-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]