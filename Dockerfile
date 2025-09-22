FROM openjdk:21-jdk-slim

EXPOSE 8070

ENV APP_HOME=/usr/src/app
WORKDIR $APP_HOME

# ✅ Copy final app.jar instead of snapshot
COPY target/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
