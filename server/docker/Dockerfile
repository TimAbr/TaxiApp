FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Копируем shadow JAR (который соберет Gradle)
COPY build/libs/*-all.jar app.jar
# Открываем порт 8080
EXPOSE 8080
# Запуск приложения
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
