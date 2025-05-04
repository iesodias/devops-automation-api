# Imagem base com JDK 17
FROM amazoncorretto:17-alpine

# Diretório de trabalho no container
WORKDIR /app

# Copia o JAR para dentro do container
COPY target/java-api-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta usada pelo Spring Boot
EXPOSE 8081

# Comando para rodar o JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
