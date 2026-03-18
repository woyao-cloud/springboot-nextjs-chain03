# 部署指南

## 环境配置

| 环境 | 用途 | 访问 |
|------|------|------|
| 开发 | 本地开发 | 本地网络 |
| 测试 | CI/CD 测试 | 内部网络 |
| 预发布 | 生产验证 | 受限访问 |
| 生产 | 正式运行 | 公共访问 |

## 容器化部署

### 开发环境
```bash
docker-compose -f docker-compose.dev.yml up -d
```

### 生产环境
- **后端**: 3 副本, 1GB 内存限制 (JVM 调优)
- **前端**: 3 副本, 256MB 内存限制
- **PostgreSQL**: 主从架构
- **Redis**: 集群模式

## Spring Boot 容器配置

### Dockerfile (多阶段构建)
```dockerfile
# 构建阶段
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### JVM 生产参数
```bash
java -Xms512m -Xmx1g \
     -XX:+UseG1GC \
     -XX:+UseContainerSupport \
     -Dspring.profiles.active=prod \
     -jar app.jar
```

## CI/CD 流程

1. 代码提交触发构建
2. 运行测试套件 (Maven/Gradle)
3. 构建 Docker 镜像
4. 推送镜像到仓库
5. 部署到 Kubernetes
6. 运行冒烟测试

## 监控告警

- **指标采集**: Micrometer + Prometheus
- **可视化**: Grafana (JVM 指标、HTTP 端点)
- **日志聚合**: ELK Stack 或 Loki
- **链路追踪**: Zipkin / Jaeger (Spring Cloud Sleuth)
- **错误追踪**: Sentry

## 健康检查

Spring Boot Actuator 端点:
- `/actuator/health` - 健康状态
- `/actuator/metrics` - 应用指标
- `/actuator/info` - 应用信息
- `/actuator/prometheus` - Prometheus 格式指标
