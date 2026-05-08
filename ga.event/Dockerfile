# 1. Etapa de Build do projeto
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# A flag -Dmaven.resources.skip=true foi removida para garantir que os arquivos estáticos (HTML) sejam incluídos.
RUN mvn clean package -DskipTests

# 2. Etapa de Execução
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]