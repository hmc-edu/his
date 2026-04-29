FROM gradle:jdk17-alpine AS build
WORKDIR /app
COPY . /app
RUN gradle -b build.gradle -x test clean bootJar --info

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
