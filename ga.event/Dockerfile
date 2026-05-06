# 1. Etapa de Build do projeto
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Etapa de Execução com imagem slim e segura
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Define a porta exposta na nuvem
EXPOSE 8080

# Inicializa o container
ENTRYPOINT ["java", "-jar", "app.jar"]