FROM openjdk:24

WORKDIR /app

COPY target/backend-0.0.1-SNAPSHOT.jar /app/backend.jar

ENTRYPOINT ["java", "-jar", "/app/backend.jar"]