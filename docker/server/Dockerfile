# 多阶段构建 — 通用 SpringBoot 微服务 Dockerfile
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY server-common/pom.xml server-common/
COPY server-gateway/pom.xml server-gateway/
COPY server-system/pom.xml server-system/
COPY server-base/pom.xml server-base/
COPY server-stock/pom.xml server-stock/
COPY server-intelligence/pom.xml server-intelligence/
COPY server-report/pom.xml server-report/

# 先下载依赖
RUN mvn dependency:go-offline -B -q

# 复制源码并构建全部模块
COPY . .
RUN mvn clean package -DskipTests -q

# ==================== 各服务镜像 ====================
# Gateway
FROM eclipse-temurin:17-jre-alpine AS gateway
WORKDIR /app
COPY --from=builder /app/server-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# System
FROM eclipse-temurin:17-jre-alpine AS system
WORKDIR /app
COPY --from=builder /app/server-system/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

# Base
FROM eclipse-temurin:17-jre-alpine AS base
WORKDIR /app
COPY --from=builder /app/server-base/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stock
FROM eclipse-temurin:17-jre-alpine AS stock
WORKDIR /app
COPY --from=builder /app/server-stock/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]

# Intelligence
FROM eclipse-temurin:17-jre-alpine AS intelligence
WORKDIR /app
COPY --from=builder /app/server-intelligence/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]

# Report
FROM eclipse-temurin:17-jre-alpine AS report
WORKDIR /app
COPY --from=builder /app/server-report/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
