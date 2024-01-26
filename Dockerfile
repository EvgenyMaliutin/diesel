FROM openjdk:17-jdk-slim
COPY target/*.jar diesel.jar
ENTRYPOINT ["java", "-jar", "diesel.jar"]