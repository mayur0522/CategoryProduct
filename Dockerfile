# Use OpenJDK 21 slim image
FROM openjdk:21-jdk-slim

# Set working directory
ENV APP_HOME=/usr/src/app
WORKDIR $APP_HOME

# Create logs folder
RUN mkdir -p CategoryProduct/logs

# Copy Spring Boot JAR (make sure the JAR name matches your Maven build)
COPY target/springboot-app-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8070 to match application.yml
EXPOSE 8070

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
