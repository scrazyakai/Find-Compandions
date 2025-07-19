# 构建阶段
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
# 添加网络检测和Maven版本验证
RUN ping -c 3 repo.maven.apache.org || echo "网络连接存在问题"
RUN mvn -version
# 使用国内镜像源加速依赖下载
RUN mkdir -p ~/.m2 && echo '<settings><mirrors><mirror><id>aliyunmaven</id><mirrorOf>*</mirrorOf><name>阿里云公共仓库</name><url>https://maven.aliyun.com/repository/public</url></mirror></mirrors></settings>' > ~/.m2/settings.xml
# 执行Maven打包，添加详细输出
RUN mvn clean package -Dmaven.test.skip=true -X || { echo "Maven构建失败"; exit 1; }

# 运行阶段
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

