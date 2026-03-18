# 数据库设计文档

## 核心表结构

### users - 用户表
```sql
id UUID PRIMARY KEY
username VARCHAR(50) UNIQUE NOT NULL
email VARCHAR(255) UNIQUE NOT NULL
password_hash VARCHAR(255) NOT NULL
first_name VARCHAR(100)
last_name VARCHAR(100)
is_active BOOLEAN DEFAULT true
is_verified BOOLEAN DEFAULT false
last_login_at TIMESTAMP
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
updated_at TIMESTAMP
```

### roles - 角色表
```sql
id UUID PRIMARY KEY
name VARCHAR(50) UNIQUE NOT NULL
description TEXT
is_default BOOLEAN DEFAULT false
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

### permissions - 权限表
```sql
id UUID PRIMARY KEY
name VARCHAR(100) UNIQUE NOT NULL
resource VARCHAR(50) NOT NULL
action VARCHAR(50) NOT NULL
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

### 关联表
- **user_roles**: user_id + role_id
- **role_permissions**: role_id + permission_id

### 审计表
- **audit_logs**: 记录所有操作变更
- **user_sessions**: 管理用户会话
- **login_attempts**: 登录安全审计

## JPA 实体映射

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
```

## 迁移规范

- 使用 **Flyway** 管理数据库迁移
- 脚本位置: `src/main/resources/db/migration/`
- 命名格式: `V{version}__{description}.sql`
  - 例如: `V1__create_users_table.sql`
- 每个版本必须有对应的迁移脚本
- 支持回滚: `U{version}__{description}.sql` (可选)

## 开发/测试环境

- **开发**: postgresql数据库，自动执行 schema.sql/data.sql
- **测试**: @DataJpaTest + @AutoConfigureTestDatabase
- **生产**: PostgreSQL，Flyway 管理迁移
