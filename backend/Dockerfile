FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . /app
RUN chmod +x gradlew && ./gradlew --no-daemon -x test clean bootJar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
