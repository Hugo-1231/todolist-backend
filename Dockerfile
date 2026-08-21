FROM maven:3.9-amazoncorretto-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

FROM amazoncorretto:17
WORKDIR /app
# 把 todolist-backend-1.0.0.jar 换成实际的文件名！
COPY --from=builder /app/target/todolist-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xms128m", "-Xmx300m", "-jar", "app.jar"]