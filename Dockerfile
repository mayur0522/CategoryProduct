# Use Java 21 slim image
FROM openjdk:21-jdk-slim
WORKDIR /app

# Accept JAR file as build argument
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# Expose application port
EXPOSE 8070

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","/app/app.jar"]
