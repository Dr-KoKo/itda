FROM openjdk:17-slim

COPY resource-server/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
