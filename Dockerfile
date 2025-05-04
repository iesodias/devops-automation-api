# Etapa 1: Build
FROM maven:3.9.6-amazoncorretto-17 AS builder

WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM amazoncorretto:17-alpine

WORKDIR /app
COPY --from=builder /build/target/java-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
