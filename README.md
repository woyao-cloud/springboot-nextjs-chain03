# springboot-nextjs-chain03

后端
### 常用命令
```bash
# 启动应用
./mvnw spring-boot:run
clean install -DskipTests
# 运行测试
./mvnw test

# 打包
./mvnw clean package

# 生成测试报告
./mvnw jacoco:report

spring-boot:run -Dspring-boot.run.profiles=dev
```
### 后端 (Java/Spring Boot)
```bash
# 代码格式检查
./mvnw spotless:check

# 静态代码分析
./mvnw checkstyle:check

# 编译
./mvnw compile

# 单元测试 + 覆盖率
./mvnw test jacoco:report

# 集成测试
./mvnw verify
```

### 前端 (TypeScript/Next.js)
```bash
# 代码格式检查
npm run lint

# 类型检查
npm run type-check

# 测试 + 覆盖率
npm run test:coverage
```