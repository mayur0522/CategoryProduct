FROM openjdk:17-jdk-slim

EXPOSE 8070

ENV APP_HOME=/usr/src/app
WORKDIR $APP_HOME

COPY target/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
