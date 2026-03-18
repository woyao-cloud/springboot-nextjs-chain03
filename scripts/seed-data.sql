-- 种子数据脚本
-- 此脚本用于插入测试数据

\c usermanagement;

-- 插入测试用户（密码都是: Password123）
-- 密码使用 BCrypt 加密: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO users (id, username, email, password_hash, first_name, last_name, is_active, is_verified, created_at)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System', 'Administrator', true, true, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440001', 'zhangsan', 'zhangsan@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张', '三', true, true, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440002', 'lisi', 'lisi@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '李', '四', true, true, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440003', 'wangwu', 'wangwu@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '王', '五', true, false, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440004', 'zhaoliu', 'zhaoliu@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '赵', '六', false, false, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440005', 'superadmin', 'superadmin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Super', 'Admin', true, true, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440006', 'user1', 'user1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Test', 'User1', true, true, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440007', 'user2', 'user2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Test', 'User2', true, true, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440008', 'guest', 'guest@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Guest', 'User', true, true, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440009', 'manager', 'manager@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Manager', 'User', true, true, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- 分配角色给用户

-- superadmin -> SUPERADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'superadmin' AND r.name = 'SUPERADMIN'
ON CONFLICT DO NOTHING;

-- admin -> ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- zhangsan, lisi, wangwu, zhaoliu -> USER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username IN ('zhangsan', 'lisi', 'wangwu', 'zhaoliu', 'user1', 'user2')
AND r.name = 'USER'
ON CONFLICT DO NOTHING;

-- guest -> GUEST
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'guest' AND r.name = 'GUEST'
ON CONFLICT DO NOTHING;

-- manager -> ADMIN + USER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'manager' AND r.name IN ('ADMIN', 'USER')
ON CONFLICT DO NOTHING;

-- 插入一些审计日志样本数据
INSERT INTO audit_logs (user_id, action, resource_type, resource_id, old_value, new_value, ip_address, created_at)
SELECT
    u.id,
    'USER_CREATED',
    'users',
    u.id::text,
    null,
    jsonb_build_object('username', u.username, 'email', u.email),
    '127.0.0.1'::inet,
    u.created_at
FROM users u
WHERE u.created_at > CURRENT_TIMESTAMP - INTERVAL '1 day'
ON CONFLICT DO NOTHING;

-- 打印种子数据插入完成信息
SELECT 'Seed data inserted successfully!' AS status;
SELECT COUNT(*) AS total_users FROM users;
SELECT COUNT(*) AS total_roles FROM roles;
SELECT COUNT(*) AS total_permissions FROM permissions;
