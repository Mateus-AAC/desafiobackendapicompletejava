FROM openjdk:17-jdk-alpine

RUN mkdir /app

WORKDIR /app

COPY build/libs/*.jar app.jar
COPY resources/changelog-default.xml ./resources/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
