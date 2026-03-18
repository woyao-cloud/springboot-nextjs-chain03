# Docker 本地开发环境配置

## 概述

使用 Docker Compose 配置本地开发环境，包含：
- **PostgreSQL 15** - 主数据库
- **Redis 6.2** - 缓存与会话存储
- **Spring Boot 后端** (可选)

## 快速开始

### 1. 启动基础设施

```bash
# 使用 Makefile
make infra

# 或直接运行 docker-compose
docker-compose up -d postgres redis
```

### 2. 验证服务状态

```bash
docker-compose ps
```

### 3. 查看日志

```bash
# 所有服务日志
make logs

# 数据库日志
make logs-db
```

### 4. 停止服务

```bash
make infra-down
```

## 服务访问信息

| 服务 | 地址 | 端口 | 凭证 |
|------|------|------|------|
| PostgreSQL | localhost | 5432 | postgres/postgres |
| Redis | localhost | 6379 | redis123 |

## 数据库配置

### 数据库初始化

首次启动时自动执行：
- `scripts/init-db.sql` - 创建表结构、索引、触发器、默认角色和权限
- `scripts/seed-data.sql` - 插入测试用户数据

### 预置用户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| superadmin | Password123 | SUPERADMIN |
| admin | Password123 | ADMIN |
| manager | Password123 | ADMIN, USER |
| zhangsan | Password123 | USER |
| lisi | Password123 | USER |
| guest | Password123 | GUEST |

## 后端开发配置

### 使用 Docker 基础设施 + 本地后端

1. 启动基础设施：
```bash
make infra
```

2. 配置环境变量 (`.env` 或直接在命令行):
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=usermanagement
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export REDIS_HOST=localhost
export REDIS_PORT=6379
```

3. 启动本地后端：
```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 使用 Docker 运行完整环境

```bash
# 构建后端镜像
make build-backend

# 启动所有服务（包含后端和前端）
make start
```

## 常用命令

```bash
# 进入数据库 shell
make db-shell

# 进入 Redis shell
make redis-shell

# 重置数据库（删除所有数据并重新初始化）
make db-reset

# 运行后端测试
make test-backend

# 运行前端测试
make test-frontend
```

## 数据持久化

数据通过 Docker volumes 持久化：
- `postgres_data` - PostgreSQL 数据
- `redis_data` - Redis 数据

查看 volumes：
```bash
docker volume ls
```

## 故障排除

### 端口被占用

如果 5432 或 6379 端口被占用，修改 `docker-compose.yml` 中的端口映射：
```yaml
ports:
  - "5433:5432"  # 使用 5433 代替 5432
```

### 连接问题

检查服务健康状态：
```bash
docker-compose ps
```

查看详细日志：
```bash
docker-compose logs postgres
docker-compose logs redis
```

### 数据库初始化失败

删除 volume 并重新启动：
```bash
docker-compose down -v
docker volume rm springboot-nextjs-chain03_postgres_data
make infra
```
