# 认证授权流程

## JWT 认证

- **访问令牌**: 30 分钟有效期
- **刷新令牌**: 7 天有效期
- **算法**: HS256
- **实现**: io.jsonwebtoken (jjwt) 库

## 核心流程

### 登录流程
1. 接收登录请求 (username/password)
2. Spring Security AuthenticationManager 验证凭证
3. UserDetailsService 加载用户信息
4. 生成访问令牌 + 刷新令牌 (JwtTokenProvider)
5. 创建会话记录 (可选)
6. 返回令牌对

### 刷新流程
1. 验证刷新令牌 (签名、过期时间)
2. 从令牌中提取用户信息
3. 生成新访问令牌
4. 返回新令牌

### 登出流程
1. 验证访问令牌 (JwtAuthenticationFilter)
2. 将令牌加入黑名单 (Redis)
3. 清除 SecurityContext
4. 返回成功响应

## RBAC 权限模型

**权限格式**: `{resource}:{action}` (Spring Security 标准格式)

| 资源 | 操作 |
|------|------|
| users | create, read, update, delete, list |
| roles | create, read, update, delete, list |
| permissions | read, list |

**默认角色**: SUPERADMIN, ADMIN, USER, GUEST

**权限注解使用:**
```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAuthority('users:read')")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
```

## 安全策略

- **密码哈希**: BCryptPasswordEncoder (Spring Security)
- **账户锁定**: 5 次失败后锁定 15 分钟 (AuthenticationFailureHandler)
- **速率限制**: 登录 10/小时/IP (Bucket4j 或自定义 Filter)
- **HTTPS 强制**: SecurityConfig 配置
- **CORS 配置**: 允许的源、方法、头部

## Spring Security 配置

**核心组件:**
- `SecurityFilterChain`: 安全过滤器链配置
- `JwtAuthenticationFilter`: JWT 认证过滤器
- `JwtTokenProvider`: 令牌生成与验证
- `UserDetailsService`: 用户详情加载
- `PasswordEncoder`: 密码加密

**配置要点:**
- 禁用 CSRF (JWT 无状态)
- 配置无状态会话管理
- 添加 JWT 过滤器到过滤器链
- 配置权限规则
