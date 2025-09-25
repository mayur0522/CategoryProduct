FROM openjdk:21-jdk-slim

WORKDIR /usr/src/app

# Create logs folder
RUN mkdir -p CategoryProduct/logs

# Copy the JAR built by Maven
COPY target/app.jar app.jar

# Expose port
EXPOSE 8070

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
