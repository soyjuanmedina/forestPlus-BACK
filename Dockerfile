FROM eclipse-temurin:17-jdk-alpine
ARG WAR_FILE=target/*.war
COPY ${WAR_FILE} app.war
ENTRYPOINT ["java", "-jar", "/app.war"]