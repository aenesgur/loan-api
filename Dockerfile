# 1. Build
# maven'ın resmi imajını kullanın ve Java 21 desteği için JDK'yı ayrı belirtin
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Run
# eclipse-temurin'in resmi JDK 21 imajını kullanın
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/loan-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 5051
ENTRYPOINT ["java", "-jar", "app.jar"]