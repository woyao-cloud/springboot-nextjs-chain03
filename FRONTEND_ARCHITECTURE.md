# 前端架构设计

## 技术栈

- Next.js 14 (App Router)
- TypeScript 5+
- Tailwind CSS + shadcn/ui
- Zustand (状态管理)
- React Hook Form + Zod
- Axios

## 项目结构

```
frontend/
├── app/                    # 页面路由
│   ├── (auth)/            # 认证路由组
│   ├── (dashboard)/       # 仪表板路由组
│   ├── api/               # API 路由
│   └── layout.tsx         # 根布局
├── components/
│   ├── ui/                # shadcn 组件
│   ├── forms/             # 表单组件
│   ├── layout/            # 布局组件
│   └── data-display/      # 数据展示
├── lib/
│   ├── api/               # API 客户端
│   ├── utils/             # 工具函数
│   └── schemas/           # Zod 验证
├── stores/                # 状态管理
└── types/                 # 类型定义
```

## 核心设计

- 原子化组件设计
- 全面 TypeScript 类型安全
- SSR/SSG 优化性能
- 路由保护中间件

## 认证功能模块

### 登录功能

**组件:**
- `app/(auth)/login/page.tsx` - 登录页面
- `components/forms/LoginForm.tsx` - 登录表单组件

**API 对接:**
- `POST /api/v1/auth/login` - 用户登录
- 请求体: `{ username: string, password: string }`
- 响应: `{ data: { accessToken: string, refreshToken: string, user: User } }`

**功能要点:**
- 表单验证: 用户名/密码必填 (Zod 验证)
- 错误处理: 显示后端错误信息 (如: 用户名或密码错误)
- 登录成功后: 存储 token 到 localStorage, 更新 Zustand auth store, 跳转到仪表盘
- 速率限制提示: 登录失败次数过多时显示锁定提示

### 注册功能

**组件:**
- `app/(auth)/register/page.tsx` - 注册页面
- `components/forms/RegisterForm.tsx` - 注册表单组件

**API 对接:**
- `POST /api/v1/auth/register` - 用户注册
- 请求体: `{ username: string, email: string, password: string }`
- 响应: `{ data: { id: number, username: string, email: string } }`

**功能要点:**
- 表单验证:
  - 用户名: 3-20 字符, 字母数字下划线 (Zod)
  - 邮箱: 有效邮箱格式
  - 密码: 最少 8 字符, 包含大小写字母和数字
  - 确认密码: 与密码一致
- 错误处理: 用户名/邮箱已存在提示
- 注册成功: 自动登录或跳转到登录页

### 令牌刷新机制

**API 对接:**
- `POST /api/v1/auth/refresh` - 刷新访问令牌
- 请求头: `Authorization: Bearer {refreshToken}`
- 响应: `{ data: { accessToken: string, refreshToken: string } }`

**实现要点:**
- Axios 拦截器: 401 响应时自动刷新令牌
- 刷新队列: 并发请求时只刷新一次
- 刷新失败: 清除登录状态, 跳转到登录页

### 当前用户信息

**API 对接:**
- `GET /api/v1/auth/me` - 获取当前登录用户信息
- 请求头: `Authorization: Bearer {accessToken}`
- 响应: `{ data: { id: number, username: string, email: string, roles: Role[] } }`

### 权限控制

**类型定义 (types/auth.ts):**
```typescript
interface User {
  id: number;
  username: string;
  email: string;
  roles: Role[];
}

interface Role {
  id: number;
  name: string;
  permissions: Permission[];
}

interface Permission {
  id: number;
  resource: string;
  action: string;
}
```

**权限检查工具 (lib/utils/permissions.ts):**
- `hasRole(user: User, role: string): boolean`
- `hasPermission(user: User, resource: string, action: string): boolean`
- `hasAnyRole(user: User, roles: string[]): boolean`

**受保护组件:**
- 页面级保护: `middleware.ts` 路由保护
- 组件级保护: `<RequireRole roles={['ADMIN']}>` 组件
- 按钮级保护: `usePermission()` hook

## 状态管理 (stores/auth.ts)

```typescript
interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  login: (credentials: LoginCredentials) => Promise<void>;
  logout: () => Promise<void>;
  refreshAccessToken: () => Promise<void>;
  setUser: (user: User) => void;
}
```

## 开发命令

```bash
npm run dev      # 开发
npm run build    # 构建
npm run test     # 测试
npm run lint     # 检查
```
