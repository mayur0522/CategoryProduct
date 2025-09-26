FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/CategoryProduct-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8070
ENTRYPOINT ["java","-jar","/app/app.jar"]
