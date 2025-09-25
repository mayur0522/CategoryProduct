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

# Pass DB credentials via environment variables at runtime
# These variables will be injected from Jenkins pipeline 
ENV DB_USERNAME=""
ENV DB_PASSWORD=""
ENV DB_HOST=""
ENV DB_PORT=""
ENV DB_NAME=""

# Run the JAR using environment variables
ENTRYPOINT ["sh", "-c", "java -Dspring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME} -Dspring.datasource.username=${DB_USERNAME} -Dspring.datasource.password=${DB_PASSWORD} -jar app.jar"]
