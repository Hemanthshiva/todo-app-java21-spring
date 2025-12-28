# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Create directory for SQLite DB
VOLUME /data

# Set environment variable so Spring picks up volume path
ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/data/todo.db

# Expose port
EXPOSE 8091

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
