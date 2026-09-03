# Etapa 1: Compilación de la aplicación Spring Boot con Maven y Java 17
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ligera para ejecución en Render
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/prog-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
