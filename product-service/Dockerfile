# Build Stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Step 1: Build common-lib first
COPY common-lib/pom.xml ./common-lib/
COPY common-lib/src ./common-lib/src
WORKDIR /app/common-lib
RUN mvn clean install -DskipTests

# Step 2: Now build product-service (paths need product-service/ prefix)
WORKDIR /app
COPY product-service/pom.xml ./product-service/
WORKDIR /app/product-service
RUN mvn dependency:go-offline -B
COPY product-service/src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/product-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]