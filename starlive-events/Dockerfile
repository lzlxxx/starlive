# 使用 JDK 17 Alpine 作为基础镜像
FROM openjdk:17-alpine

# 设置工作目录
WORKDIR /app

# 复制应用 jar 包
ADD target/starlive-events-0.0.1-SNAPSHOT.jar app.jar

# 创建数据和日志目录
RUN mkdir -p /data/events/messages /var/log/starlive

# 设置时区
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    rm -rf /var/cache/apk/*

# 设置 JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx512m"

# 暴露端口
EXPOSE 9898

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
