# 构建阶段 - 用 Amazon Corretto（AWS 的 JDK，多架构支持好）
FROM maven:3.9-amazoncorretto-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

# 运行阶段
FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xms128m", "-Xmx300m", "-jar", "app.jar"]