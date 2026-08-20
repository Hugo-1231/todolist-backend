# ============================================
# 阶段一：使用 Eclipse Temurin JDK 1.8 作为构建环境
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /app

# 1. 先复制 Maven 包装器和配置文件（利用 Docker 缓存层）
#    这些文件不常变动，Docker 会缓存这一层，下次构建时跳过
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# 2. 下载所有依赖（离线模式，-B 表示批处理模式不交互）
#    这一步单独做是为了缓存依赖，代码改动后不需要重新下载
RUN ./mvnw dependency:go-offline -B

# 3. 复制源代码（src 目录经常变动，放在后面）
COPY src src

# 4. 构建项目，跳过测试（-DskipTests）加速构建
#    生成的 jar 文件在 target/ 目录下
RUN ./mvnw clean package -DskipTests

# ============================================
# 阶段二：使用更小的 JRE 镜像运行（多阶段构建）
# ============================================
FROM eclipse-temurin:17-jre

# 设置工作目录
WORKDIR /app

# 5. 从构建阶段复制生成的 jar 包
#    注意：将 todolist-0.0.1-SNAPSHOT.jar 替换为你实际的 jar 文件名！
#    你可以在本地运行 mvnw clean package 后查看 target/ 目录下的文件名
COPY --from=builder /app/target/todo-backend-1.0.0.jar app.jar

# 6. 暴露应用端口（根据你的 application.properties / application.yml 调整）
#    例如：server.port=8080
EXPOSE 8080

# 7. 设置 JVM 参数（JDK 1.8 推荐配置）
#    -Xmx512m: 最大堆内存 512MB（Render 免费版内存有限）
#    -Xms256m: 初始堆内存 256MB
#    -Djava.security.egd=file:/dev/./urandom: 加速随机数生成
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# 8. 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]