# 后端架构设计

## 技术栈

- Spring Boot 3.2 + Java 21
- Spring Data JPA + Hibernate 6.x
- PostgreSQL Database
- Spring Security + JWT
- Redis (缓存)
- Maven / Gradle

## 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/usermanagement/
│   │   │   ├── UserManagementApplication.java  # 应用入口
│   │   │   ├── config/                         # 配置类
│   │   │   ├── controller/                     # API 控制器 (REST)
│   │   │   ├── service/                        # 业务服务层
│   │   │   │   └── impl/                       # 服务实现
│   │   │   ├── repository/                     # 数据访问层
│   │   │   ├── entity/                         # JPA 实体
│   │   │   ├── dto/                            # 数据传输对象
│   │   │   ├── mapper/                         # 对象映射
│   │   │   ├── security/                       # 安全、认证、权限
│   │   │   ├── exception/                      # 全局异常处理
│   │   │   └── util/                           # 工具类
│   │   └── resources/
│   │       ├── application.yml                 # 应用配置
│   │       ├── application-dev.yml             # 开发环境配置
│   │       ├── application-prod.yml            # 生产环境配置
│   │       └── db/migration/                   # Flyway 迁移脚本
│   └── test/                                   # 测试代码
├── pom.xml (或 build.gradle)                   # 依赖配置
└── Dockerfile                                  # 容器配置
```

## 分层架构

1. **Controller 层**: HTTP 请求处理，输入验证，响应封装
2. **Service 层**: 业务逻辑协调，事务管理
3. **Repository 层**: 数据访问，JPA 查询
4. **Entity 层**: 领域模型，JPA 映射

## 安全设计

- JWT 认证 (访问令牌 + 刷新令牌)
- Spring Security RBAC 权限控制
- 速率限制 (Bucket4j / Resilience4j)
- SQL 注入防护 (JPA 参数化查询)
- XSS 防护 (HttpSecurity 配置)
- CORS 配置

## 性能优化

- 数据库连接池 (HikariCP)
- Redis 缓存 (@Cacheable)
- 异步处理 (@Async)
- 查询优化 (JPQL / 原生 SQL)
- 分页查询 (Pageable)

## 依赖管理

**核心依赖:**
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation
- spring-boot-starter-data-redis
- jjwt (JWT 处理)
- flyway-core (数据库迁移)
- postgresql (生产数据库)
- postgresql (开发/测试数据库)

**测试依赖:**
- spring-boot-starter-test
- spring-security-test
