# Stage 1: Build the application using Maven and Java 25
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /workspace

# Copy your configuration and source code
COPY pom.xml ./
COPY src ./src

# Compile and package the application (skipping tests for speed)
RUN mvn -B -DskipTests package --no-transfer-progress

# Stage 2: Create the lightweight runtime environment using Java 25
FROM eclipse-temurin:25-jdk
WORKDIR /app

# Copy the compiled .jar file from the builder stage
COPY --from=builder /workspace/target/*.jar app.jar

# Expose the standard Spring Boot port
EXPOSE 8080

# Security best practice: Run the app as a non-root user
RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]