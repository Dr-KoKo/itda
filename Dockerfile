FROM openjdk:17-slim

COPY resource-service/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
