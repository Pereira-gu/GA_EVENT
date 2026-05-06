# 1. Etapa de Build do projeto
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Adicionando -Dmaven.resources.skip=true para ignorar o erro do plugin de recursos
RUN mvn clean package -DskipTests -Dmaven.resources.skip=true

# 2. Etapa de Execução
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]