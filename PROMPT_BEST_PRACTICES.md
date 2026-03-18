# 提示词使用最佳实践指南

## 概述

本项目包含 10 个核心提示词文件，构成完整的全栈用户管理系统知识库。本指南说明如何高效使用这些提示词与 Claude Code 协作。

---

## 提示词文件分类

### 1. 项目入口文件
| 文件 | 用途 | 何时引用 |
|------|------|----------|
| `CLAUDE.md` | 项目配置入口 | 每次对话自动加载 |
| `README.md` | 项目说明 | 新成员 onboarding |

### 2. 架构设计类
| 文件 | 用途 | 何时引用 |
|------|------|----------|
| `BACKEND_ARCHITECTURE.md` | 后端架构规范 | 开发后端功能时 |
| `FRONTEND_ARCHITECTURE.md` | 前端架构规范 | 开发前端功能时 |
| `DATABASE_SCHEMA.md` | 数据库设计 | 涉及数据模型变更时 |

### 3. 接口与流程类
| 文件 | 用途 | 何时引用 |
|------|------|----------|
| `API_SPECIFICATION.md` | API 接口规范 | 开发/修改 API 时 |
| `AUTHENTICATION_FLOW.md` | 认证授权流程 | 涉及登录/权限/安全时 |

### 4. 工程实践类
| 文件 | 用途 | 何时引用 |
|------|------|----------|
| `AGENTS.md` | 多代理协作配置 | 分配任务给特定角色时 |
| `TESTING_STRATEGY.md` | 测试策略 | 编写测试代码时 |
| `DEVELOPMENT_WORKFLOW.md` | 开发流程 | Git 操作/代码审查时 |
| `DEPLOYMENT_GUIDE.md` | 部署指南 | CI/CD/容器化配置时 |

---

## 使用步骤

### 步骤 1: 明确任务类型

首先确定您的任务属于哪个阶段：

```
需求分析 → 架构设计 → 编码实现 → 测试验证 → 部署上线
```

### 步骤 2: 选择相关提示词

根据任务类型，选择对应的提示词文件：

| 任务类型 | 相关提示词 |
|----------|-----------|
| 创建新功能 | CLAUDE.md + AGENTS.md + 对应架构文档 |
| 修改数据库 | DATABASE_SCHEMA.md + BACKEND_ARCHITECTURE.md |
| 开发 API | API_SPECIFICATION.md + AUTHENTICATION_FLOW.md |
| 编写测试 | TESTING_STRATEGY.md + 对应架构文档 |
| 配置 CI/CD | DEPLOYMENT_GUIDE.md + DEVELOPMENT_WORKFLOW.md |

### 步骤 3: 构建有效提示

#### 推荐格式

```
[角色] 作为<代理角色>，请帮我<任务描述>

[上下文] 参考以下文档：
- <相关提示词文件>

[具体要求]
1. <要求 1>
2. <要求 2>

[约束条件]
- <约束 1>
- <约束 2>
```

#### 示例 1: 后端功能开发

```
作为后端工程师，请帮我实现用户管理模块的 CRUD API。

参考以下文档：
- BACKEND_ARCHITECTURE.md 的分层架构规范
- API_SPECIFICATION.md 的响应格式和端点定义
- DATABASE_SCHEMA.md 的 users 表结构
- AUTHENTICATION_FLOW.md 的 RBAC 权限配置

要求：
1. 实现完整的 Controller → Service → Repository 分层
2. 包含输入验证和异常处理
3. 添加 @PreAuthorize 权限注解
4. 单元测试覆盖率 > 85%

约束：
- 使用已有的 User 实体类
- 遵循现有的异常处理模式
```

#### 示例 2: 前端页面开发

```
作为前端工程师，请帮我创建用户列表页面。

参考以下文档：
- FRONTEND_ARCHITECTURE.md 的项目结构和设计原则
- API_SPECIFICATION.md 的用户端点定义

要求：
1. 使用 shadcn/ui 的 Table 组件
2. 实现分页功能
3. 集成 Zustand 状态管理
4. 添加加载状态和错误处理
```

#### 示例 3: 数据库变更

```
作为数据库工程师，请帮我添加用户角色关联表。

参考 DATABASE_SCHEMA.md 中：
- 现有的 users 和 roles 表结构
- 关联表设计模式
- Flyway 迁移规范

要求：
1. 创建 user_roles 关联表
2. 编写 Flyway 迁移脚本 V2__add_user_roles.sql
3. 更新 JPA 实体类添加 @ManyToMany 关系
```

---

## 多代理协作模式

### 模式 1: 顺序执行（适合复杂功能）

```
架构师 → 数据库工程师 → 后端工程师 → 前端工程师 → 测试工程师
```

**使用方式**:
1. 先让架构师设计接口
2. 数据库工程师建模
3. 前后端并行开发
4. 测试工程师验证

### 模式 2: 单一代理（适合简单任务）

直接指定角色和所需文档：

```
作为测试工程师，参考 TESTING_STRATEGY.md，
为 UserService 编写单元测试。
```

### 模式 3: 专家咨询（适合问题排查）

```
参考 DEPLOYMENT_GUIDE.md 的监控告警章节，
帮我配置 Spring Boot Actuator 和 Prometheus 集成。
```

---

## 提示词引用技巧

### 技巧 1: 引用具体章节

```
参考 DATABASE_SCHEMA.md 中"JPA 实体映射"部分的示例代码，
帮我完善 User 实体类。
```

### 技巧 2: 组合多个文档

```
请同时参考：
1. AUTHENTICATION_FLOW.md 的 JWT 配置
2. BACKEND_ARCHITECTURE.md 的安全设计章节
3. TESTING_STRATEGY.md 的安全测试要求

帮我实现登录接口并编写测试。
```

### 技巧 3: 指出矛盾或缺失

```
我发现 DATABASE_SCHEMA.md 中定义的字段类型
与 BACKEND_ARCHITECTURE.md 中的 DTO 不一致，
请帮我统一并更新相关代码。
```

---

## 常见场景速查

### 场景 1: 新功能从零开发

```
提示词组合:
1. AGENTS.md - 了解协作流程
2. CLAUDE.md - 了解技术栈和项目结构
3. BACKEND_ARCHITECTURE.md / FRONTEND_ARCHITECTURE.md - 遵循架构规范
4. API_SPECIFICATION.md - 了解接口规范

执行顺序:
1. "作为架构师，设计用户权限模块的接口..."
2. "作为数据库工程师，创建权限相关表..."
3. "作为后端工程师，实现权限 API..."
4. "作为前端工程师，创建权限管理页面..."
5. "作为测试工程师，编写权限测试用例..."
```

### 场景 2: Bug 修复

```
提示词组合:
1. 相关架构文档 - 了解代码应该遵循的结构
2. TESTING_STRATEGY.md - 确保修复包含测试

示例:
"用户登录时遇到 401 错误，参考 AUTHENTICATION_FLOW.md
的登录流程章节，帮我排查并修复问题。"
```

### 场景 3: 代码审查

```
提示词组合:
1. DEVELOPMENT_WORKFLOW.md - 了解提交规范和审查标准
2. 相关架构文档 - 检查是否符合架构规范

示例:
"请审查这个 PR，参考 DEVELOPMENT_WORKFLOW.md 的代码审查要求
和 BACKEND_ARCHITECTURE.md 的分层架构规范。"
```

### 场景 4: 性能优化

```
提示词组合:
1. BACKEND_ARCHITECTURE.md - 性能优化章节
2. DATABASE_SCHEMA.md - 索引和查询优化
3. DEPLOYMENT_GUIDE.md - JVM 调优参数

示例:
"用户列表查询响应慢，参考 DATABASE_SCHEMA.md 和
BACKEND_ARCHITECTURE.md 的优化建议，帮我优化性能。"
```

---

## 质量检查清单

在完成任务前，确认是否满足：

- [ ] 是否参考了相关架构文档？
- [ ] 代码是否符合分层架构？
- [ ] API 是否符合规范格式？
- [ ] 是否包含适当的权限控制？
- [ ] 测试覆盖率是否达标？
- [ ] 文档是否需要同步更新？

---

## 提示词更新维护

当项目演进时，及时更新提示词文件：

1. **新增技术栈** → 更新 CLAUDE.md
2. **架构变更** → 更新对应架构文档
3. **新 API 端点** → 更新 API_SPECIFICATION.md
4. **流程调整** → 更新 DEVELOPMENT_WORKFLOW.md
5. **部署方式变更** → 更新 DEPLOYMENT_GUIDE.md

---

## 快速参考卡片

```
┌─────────────────────────────────────────────────────────────┐
│                    提示词快速选择                            │
├─────────────────────────────────────────────────────────────┤
│ 后端开发 → BACKEND_ARCHITECTURE.md + API_SPECIFICATION.md   │
│ 前端开发 → FRONTEND_ARCHITECTURE.md + API_SPECIFICATION.md  │
│ 数据库   → DATABASE_SCHEMA.md                               │
│ 认证授权 → AUTHENTICATION_FLOW.md                           │
│ 测试     → TESTING_STRATEGY.md                              │
│ 部署     → DEPLOYMENT_GUIDE.md                              │
│ Git流程  → DEVELOPMENT_WORKFLOW.md                          │
│ 多角色   → AGENTS.md                                        │
└─────────────────────────────────────────────────────────────┘
```

作为后端开发人员，希望生成数据库初始化脚本并使用docker-compose 配置本地开发环境运行PostgreSQL:15及redies:6.2