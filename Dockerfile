# ---------- Etapa 1: compila o projeto com Maven e JDK 21 ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

# ---------- Etapa 2: imagem final, so com o Java para rodar ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/respawn-api-*.jar app.jar
ENV TZ=America/Sao_Paulo
EXPOSE 8080
# O Render define a variavel PORT; a aplicacao le em server.port=${PORT:8080}
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
