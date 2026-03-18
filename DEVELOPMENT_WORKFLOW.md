# 开发工作流程

## Git 分支策略

| 分支 | 用途 |
|------|------|
| main | 生产代码 |
| develop | 开发主分支 |
| feature/* | 功能开发 |
| bugfix/* | 问题修复 |
| hotfix/* | 紧急修复 |

## 提交规范

```
<type>(<scope>): <subject>

type: feat|fix|docs|style|refactor|perf|test|chore
```

## 代码审查

- 至少 2 名审查者
- 所有检查通过
- 解决所有评论
- 使用 squash merge

## 质量门禁


## 发布流程

1. 从 develop 创建 release 分支
2. 更新版本号和 CHANGELOG
3. 合并到 main 和 develop
4. 打标签并部署

## 开发环境配置

### 后端开发
- JDK 21+
- Maven 3.9+ 或 Gradle 8+
- IDE: IntelliJ IDEA / VS Code (Spring Boot Extension Pack)
- 本地数据库: PostgreSQL 

### 后端服务
- 使用docker-compose 配置本地开发环境运行PostgreSQL:15及redies:6.2

### 数据库初始
- 生成数据库创建脚本及导入初始数据到数据库的脚本
