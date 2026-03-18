# 测试策略文档

## 测试金字塔

| 层级 | 比例 | 工具 |
|------|------|------|
| 单元测试 | 70% | JUnit 5 + Mockito |
| 集成测试 | 20% | Spring Boot Test + TestContainers |
| E2E 测试 | 10% | Playwright |

## 后端测试

### 单元测试
- **实体测试**: JPA 实体映射、约束验证
- **服务层测试**: 业务逻辑、事务边界
- **工具类测试**: 工具方法、静态方法
- **安全测试**: JWT 工具、密码编码

### 集成测试
- **Controller 测试**: @WebMvcTest + MockMvc
- **Repository 测试**: @DataJpaTest + @AutoConfigureTestDatabase
- **安全集成**: @WithMockUser、JWT 过滤器测试
- **API 端点**: @SpringBootTest + TestRestTemplate

### E2E 测试
- 完整用户流程
- 跨服务调用验证

## 前端测试

- **单元测试**: 组件渲染、Hook 逻辑、工具函数 (Jest)
- **集成测试**: API 集成、页面交互 (MSW)
- **E2E 测试**: 用户流程、跨浏览器 (Playwright)

## 覆盖率目标

- 后端: ≥ 85%
- 前端: ≥ 80%

## CI/CD 集成

1. 代码质量检查 (SonarQube / Checkstyle)
2. 单元测试
3. 集成测试
4. 端到端测试
