FROM eclipse-temurin:17-jdk-alpine
COPY target/forestplus-0.0.1-SNAPSHOT.war app.war
ENTRYPOINT ["java", "-jar", "/app.war"]