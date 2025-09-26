# Use official OpenJDK 21 slim image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR using a wildcard to handle any version
COPY target/CategoryProduct-*.jar app.jar

# Expose application port
EXPOSE 8070

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
