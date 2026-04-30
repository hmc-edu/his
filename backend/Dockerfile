# 默认用 docker.io（国际通用）。
# 国内学生若 docker.io 拉不动，可在 build 时覆盖：
#   docker compose build --build-arg BASE_REGISTRY=m.daocloud.io/docker.io
ARG BASE_REGISTRY=docker.io

FROM ${BASE_REGISTRY}/library/eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . /app
RUN chmod +x gradlew && ./gradlew --no-daemon -x test clean bootJar

FROM ${BASE_REGISTRY}/library/eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
