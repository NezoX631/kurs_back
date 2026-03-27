FROM gradle:9.3.1-jdk17 AS builder

WORKDIR /app

# Копируем только файлы для сборки
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY src src

# Собираем приложение
RUN gradle bootJar --no-daemon --info

# Финальный образ
FROM eclipse-temurin:17-jre

WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8855

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]