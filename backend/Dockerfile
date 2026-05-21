ARG BASE_REGISTRY=m.daocloud.io/docker.io

# 构建阶段
FROM ${BASE_REGISTRY}/library/eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY . .

# 修复 Windows CRLF
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

RUN ./gradlew --no-daemon -x test clean bootJar

# 运行阶段
FROM ${BASE_REGISTRY}/library/eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
