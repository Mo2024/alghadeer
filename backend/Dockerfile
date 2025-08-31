FROM openjdk:24

ENV TZ=Asia/Bahrain

WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar /app/backend.jar
ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
