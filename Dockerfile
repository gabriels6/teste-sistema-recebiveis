# syntax=docker/dockerfile:1

# --- Estagio de build: compila o jar executavel com o Gradle Wrapper ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# Copia primeiro os arquivos de build para aproveitar o cache de dependencias:
# enquanto build.gradle/settings.gradle nao mudam, esta camada e reutilizada.
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

# Copia o codigo-fonte e gera o boot jar (sem rodar os testes).
COPY src src
RUN ./gradlew --no-daemon clean bootJar

# --- Estagio de runtime: imagem enxuta apenas com o JRE e o jar ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
