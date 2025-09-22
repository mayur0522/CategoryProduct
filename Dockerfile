FROM openjdk:17-jdk-slim

EXPOSE 8070

ENV APP_HOME=/usr/src/app
WORKDIR $APP_HOME

COPY target/CategoryProduct-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
