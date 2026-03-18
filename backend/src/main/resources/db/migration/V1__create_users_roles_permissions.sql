-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    is_verified BOOLEAN DEFAULT false,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create permissions table
CREATE TABLE IF NOT EXISTS permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Create role_permissions junction table
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);
CREATE INDEX IF NOT EXISTS idx_permissions_name ON permissions(name);
CREATE INDEX IF NOT EXISTS idx_permissions_resource_action ON permissions(resource, action);

-- Insert default roles
INSERT INTO roles (name, description, is_default) VALUES
    ('SUPERADMIN', 'Full system access', false),
    ('ADMIN', 'Administrative access', false),
    ('USER', 'Standard user', true),
    ('GUEST', 'Limited access', false)
ON CONFLICT (name) DO NOTHING;

-- Insert default permissions for users resource
INSERT INTO permissions (name, resource, action) VALUES
    ('users:create', 'users', 'create'),
    ('users:read', 'users', 'read'),
    ('users:update', 'users', 'update'),
    ('users:delete', 'users', 'delete'),
    ('users:list', 'users', 'list')
ON CONFLICT (name) DO NOTHING;

-- Insert default permissions for roles resource
INSERT INTO permissions (name, resource, action) VALUES
    ('roles:create', 'roles', 'create'),
    ('roles:read', 'roles', 'read'),
    ('roles:update', 'roles', 'update'),
    ('roles:delete', 'roles', 'delete'),
    ('roles:list', 'roles', 'list')
ON CONFLICT (name) DO NOTHING;

-- Insert default permissions for permissions resource
INSERT INTO permissions (name, resource, action) VALUES
    ('permissions:read', 'permissions', 'read'),
    ('permissions:list', 'permissions', 'list')
ON CONFLICT (name) DO NOTHING;

-- Assign all permissions to SUPERADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'SUPERADMIN'
ON CONFLICT DO NOTHING;

-- Assign users and roles permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
AND (p.resource IN ('users', 'roles') OR (p.resource = 'permissions' AND p.action IN ('read', 'list')))
ON CONFLICT DO NOTHING;

-- Assign read/list permissions to USER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'USER'
AND p.resource = 'users'
AND p.action IN ('read', 'list')
ON CONFLICT DO NOTHING;

-- Assign only read own permissions to GUEST role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'GUEST'
AND p.resource = 'users'
AND p.action = 'read'
ON CONFLICT DO NOTHING;
