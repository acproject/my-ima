# 提案概览：implement-repositories

## 基本信息

| 项目 | 值 |
|------|-----|
| **变更ID** | `implement-repositories` |
| **状态** | ✅ 已验证，等待批准 |
| **任务进度** | 0/85 (0%) |
| **验证状态** | ✅ 通过 (--strict模式) |
| **创建时间** | 2026年1月13日 |

---

## 📋 提案摘要

### 为什么需要这个变更？

**问题：**
- 当前Repository接口为空或部分实现
- JooqUserRepository有编译错误
- 没有完整的数据访问层，Service层无法工作

**解决方案：**
- 实现6个核心Repository（Realm, User, Role, Permission, Client, Token, AuditLog）
- 修复JooqUserRepository编译错误
- 添加完整的CRUD操作
- 所有Repository使用JOOQ类型安全SQL查询
- 每个Repository都包含单元测试

### 影响范围

**影响的代码：**
- `com.owiseman.core.jooq.repository` - 所有接口
- `com.owiseman.core.jooq.repository.impl` - 所有实现
- `com.owiseman.core.jooq.records` - JOOQ生成的记录类

**影响的规范：**
- repository (新规范)

**前置依赖：**
- PostgreSQL数据库运行且schema已初始化
- JOOQ代码生成成功

---

## 📊 任务清单概览

### 总计：85个任务

| 阶段 | 任务数 | 描述 |
|------|--------|------|
| 1. Prerequisites | 5 | 环境准备和JOOQ代码生成 |
| 2. RealmRepository | 9 | Realm数据访问层 |
| 3. UserRepository | 14 | 用户数据访问层 |
| 4. RoleRepository | 8 | 角色数据访问层 |
| 5. PermissionRepository | 7 | 权限数据访问层 |
| 6. PolicyRepository | 7 | 策略数据访问层 |
| 7. ClientRepository | 7 | 客户端数据访问层 |
| 8. TokenRepository | 11 | Token数据访问层 |
| 9. AuditLogRepository | 6 | 审计日志数据访问层 |
| 10. Testing | 7 | 测试和验证 |
| 11. Documentation | 4 | 文档 |

### 关键任务亮点

**Prerequisites (5个任务)：**
- [ ] 1.1 确保PostgreSQL运行
- [ ] 1.2 确保database schema初始化
- [ ] 1.3 运行JOOQ代码生成
- [ ] 1.4 验证生成的类
- [ ] 1.5 验证无编译错误

**UserRepository (14个任务）- 最复杂：**
- [ ] 3.1 修复JooqUserRepository编译错误
- [ ] 3.2 更新UserRepository接口（10个方法）
- [ ] 3.3-3.12 实现所有方法（10个）
- [ ] 3.13 添加异常处理
- [ ] 3.14 编写单元测试

**Testing (7个任务）- 验证质量：**
- [ ] 10.1 创建H2测试schema
- [ ] 10.2 配置测试环境
- [ ] 10.3 为每个Repository编写测试
- [ ] 10.4 测试覆盖率 > 90%
- [ ] 10.5 运行所有测试
- [ ] 10.6 验证编译
- [ ] 10.7 手动集成测试

---

## 📖 规范定义概览

### 总计：11个需求，60+个场景

| 需求 | 场景数 | 描述 |
|------|--------|------|
| 1. Realm Repository Operations | 5 | Realm CRUD操作 |
| 2. User Repository Operations | 10 | 用户管理（包括角色和权限） |
| 3. Role Repository Operations | 8 | 角色管理（包括权限分配） |
| 4. Permission Repository Operations | 6 | 权限管理 |
| 5. Policy Repository Operations | 5 | 策略管理 |
| 6. Client Repository Operations | 8 | 客户端管理（包括认证） |
| 7. Token Repository Operations | 6 | Token生命周期管理 |
| 8. Audit Log Repository Operations | 6 | 审计日志 |
| 9. Repository Error Handling | 3 | 错误处理 |
| 10. Repository Performance | 3 | 性能优化 |

### 关键需求示例

**User Repository Operations (10个场景）：**

| 场景 | 描述 |
|------|------|
| Find user by username | 通过用户名查询用户 |
| Find user by email | 通过邮箱查询用户 |
| Create user with password hashing | 创建用户时BCrypt密码加密 |
| Assign role to user | 为用户分配角色 |
| Remove role from user | 移除用户角色 |
| Find user permissions | 查询用户所有权限（通过角色关联） |
| Find user roles | 查询用户所有角色 |
| Update user | 更新用户信息 |
| Soft delete user | 软删除用户 |
| List users with pagination | 分页查询用户列表 |

**Client Repository Operations (8个场景）：**

| 场景 | 描述 |
|------|------|
| Find client by client_id | 通过client_id查询客户端 |
| Create confidential client | 创建confidential客户端（需要secret） |
| Create public client | 创建public客户端（不需要secret） |
| Authenticate client | 客户端认证（BCrypt验证） |
| Authentication with wrong secret | 错误密码认证失败 |
| Update client | 更新客户端配置 |
| Delete client | 删除客户端 |
| List clients with pagination | 分页查询客户端列表 |

---

## 🏗️ 将要创建的代码结构

### Repository接口（8个新文件）

```
com.owiseman.core.jooq.repository/
├── RealmRepository.java          (6个方法)
├── UserRepository.java            (10个方法，已存在需扩展)
├── RoleRepository.java            (9个方法)
├── PermissionRepository.java      (7个方法，已存在）
├── PolicyRepository.java          (6个方法，已存在）
├── ClientRepository.java         (7个方法，已存在）
├── TokenRepository.java         (7个方法，已存在）
└── AuditLogRepository.java       (5个方法，新增）
```

### Repository实现（8个新文件）

```
com.owiseman.core.jooq.repository.impl/
├── JooqRealmRepository.java     (新增)
├── JooqUserRepository.java     (存在，需修复和扩展)
├── JooqRoleRepository.java     (新增)
├── JooqPermissionRepository.java (新增)
├── JooqPolicyRepository.java   (新增)
├── JooqClientRepository.java    (新增)
├── JooqTokenRepository.java    (新增)
└── JooqAuditLogRepository.java (新增)
```

### 测试文件（8个新文件）

```
com.owiseman.core.jooq.repository.impl/
├── RealmRepositoryTest.java
├── UserRepositoryTest.java
├── RoleRepositoryTest.java
├── PermissionRepositoryTest.java
├── PolicyRepositoryTest.java
├── ClientRepositoryTest.java
├── TokenRepositoryTest.java
└── AuditLogRepositoryTest.java
```

### 预计代码量

| 类型 | 文件数 | 预计行数 |
|------|--------|----------|
| Repository接口 | 8 | 150-200 |
| Repository实现 | 8 | 1500-2000 |
| 测试文件 | 8 | 800-1200 |
| **总计** | **24** | **~3000行** |

---

## 🎯 验收标准

### 必须完成
- ✅ 所有Repository接口有完整的方法签名
- ✅ 所有Repository实现完成且通过测试
- ✅ 测试覆盖率 > 90%
- ✅ 无编译错误
- ✅ 所有手动集成测试通过
- ✅ 代码遵循项目规范（openspec/project.md）

### 完成标志
- `openspec/changes/implement-repositories/tasks.md` 中所有85个任务都已勾选 `[x]`
- `mvn test` 所有测试通过
- `mvn clean compile` 编译无错误

---

## 🚧 技术细节

### 使用的技术

| 技术 | 用途 |
|------|------|
| JOOQ DSLContext | 类型安全SQL查询 |
| JOOQ Records | 数据库表映射 |
| Optional | NPE防护 |
| BCrypt | 密码加密 |
| H2 Database | 单元测试 |

### 数据库表映射

| Repository | 主表 | 关联表 |
|-----------|-------|--------|
| RealmRepository | ima_realm | - |
| UserRepository | ima_user | ima_user_role |
| RoleRepository | ima_role | ima_user_role, ima_role_permission |
| PermissionRepository | ima_permission | ima_role_permission, ima_permission_policy |
| PolicyRepository | ima_policy | ima_permission_policy |
| ClientRepository | ima_client | - |
| TokenRepository | ima_token | - |
| AuditLogRepository | ima_audit_log | - |

### 关键查询示例

**复杂查询1：查找用户所有权限**
```java
// 用户 -> 角色 -> 权限 (多表关联）
SELECT DISTINCT p.resource, p.action
FROM ima_permission p
JOIN ima_role_permission rp ON p.id = rp.permission_id
JOIN ima_role r ON rp.role_id = r.id
JOIN ima_user_role ur ON r.id = ur.role_id
WHERE ur.user_id = :userId
```

**复杂查询2：按日期范围查询审计日志**
```java
SELECT * FROM ima_audit_log
WHERE realm_id = :realmId
  AND created_at >= :start
  AND created_at <= :end
ORDER BY created_at DESC
LIMIT :limit OFFSET :offset
```

---

## 📋 实施计划

### 预计时间

| 阶段 | 任务数 | 预计时间 |
|------|--------|----------|
| Prerequisites | 5 | 0.5天 |
| RealmRepository | 9 | 0.5天 |
| UserRepository | 14 | 1天 |
| RoleRepository | 8 | 0.5天 |
| PermissionRepository | 7 | 0.5天 |
| PolicyRepository | 7 | 0.5天 |
| ClientRepository | 7 | 0.5天 |
| TokenRepository | 11 | 0.5天 |
| AuditLogRepository | 6 | 0.5天 |
| Testing | 7 | 1天 |
| Documentation | 4 | 0.5天 |
| **总计** | **85** | **2-3天** |

### 实施顺序建议

**第1天：基础设施 + 基础Repository**
- 上午：Prerequisites + RealmRepository
- 下午：UserRepository (部分）

**第2天：Repository实现**
- 上午：RoleRepository + PermissionRepository + PolicyRepository
- 下午：ClientRepository + TokenRepository + AuditLogRepository

**第3天：测试和文档**
- 上午：所有Repository测试
- 下午：集成测试 + 文档

---

## ⚠️ 风险和依赖

### 已知问题

| 问题 | 影响 | 解决方案 |
|------|------|---------|
| JOOQ代码生成失败 | 无法生成Record类 | 修复数据库连接后重试 |
| JooqUserRepository编译错误 | 无法编译 | 修复DSL引用，等待JOOQ生成 |

### 依赖关系

```
Prerequisites
    ↓
JOOQ Code Generation
    ↓
All Repository Implementations
    ↓
Unit Tests
    ↓
Integration Tests
```

### 阻塞点

如果有以下情况，无法继续：
- ❌ PostgreSQL未运行
- ❌ Database schema未初始化
- ❌ JOOQ代码生成失败
- ❌ 编译错误未修复

---

## ✅ 完成后的成果

### 交付物

1. ✅ 8个完整的Repository接口
2. ✅ 8个完整的Repository实现
3. ✅ 8个完整的测试类（覆盖率 > 90%）
4. ✅ 完整的Javadoc文档
5. ✅ 性能优化建议（索引）

### 解锁的功能

完成此变更后，可以开始：
- ✅ implement-password-service (密码服务）
- ✅ implement-jwt-service (JWT服务）
- ✅ implement-authentication-service (认证服务）

### 整体进度提升

```
当前：10% (OpenSpec提案完成）
完成后：25% (基础设施层完成）
下一阶段：核心服务层 (Password + JWT + Auth）
```

---

## 🚀 下一步

### 选项1：批准并开始实施

**前提：** 审查提案内容，确认符合需求

**行动：**
```bash
# 标记任务为进行中
# 开始按tasks.md顺序实施
# 完成一个，勾选一个
```

### 选项2：先修复依赖

**问题：** JOOQ代码生成需要数据库连接

**行动：**
```bash
# 1. 检查数据库
psql -h localhost -U postgres -d my-iam

# 2. 初始化schema
psql -h localhost -U postgres -d my-iam -f src/main/resources/sql/ima_sql.sql

# 3. 运行JOOQ代码生成
export PGPASSWORD=ac23456
mvn jooq-codegen:generate
```

### 选项3：开始下一个提案

可以并行创建下一个提案：
- `implement-password-service`
- `implement-jwt-service`
- `implement-authentication-service`

---

## 📁 相关文件

| 文件 | 路径 | 大小 |
|------|-------|------|
| 提案说明 | `openspec/changes/implement-repositories/proposal.md` | ~1KB |
| 任务清单 | `openspec/changes/implement-repositories/tasks.md` | ~4KB |
| 规范定义 | `openspec/changes/implement-repositories/specs/repository/spec.md` | ~12KB |
| 概览文档 | `PROPOSAL_OVERVIEW.md` | ~8KB |

---

## 📞 如何审查提案

### 检查清单

**提案完整性：**
- ✅ Why部分清楚说明问题和解决方案
- ✅ What Changes列出所有变更
- ✅ Impact部分清晰说明影响范围
- ✅ Prerequisites明确定义

**任务完整性：**
- ✅ 85个任务覆盖所有Repository
- ✅ 任务顺序合理（Prerequisites → 实现 → 测试）
- ✅ 每个Repository都有明确的任务
- ✅ Testing阶段确保质量

**规范完整性：**
- ✅ 11个需求覆盖所有功能
- ✅ 60+个场景详细说明行为
- ✅ Scenario格式正确（#### Scenario:）
- ✅ 使用GIVEN/WHEN/THEN结构

### 批准标准

如果满足以下条件，可以批准：
- ✅ 任务数量合理（85个）
- ✅ 规范定义完整（11个需求）
- ✅ 验收标准明确
- ✅ 预计时间合理（2-3天）
- ✅ 无高风险依赖

---

## 🎓 学习资源

**相关文档：**
- `openspec/AGENTS.md` - OpenSpec官方文档
- `OPENSPEC_WORKFLOW_GUIDE.md` - OpenSpec工作流指南
- `AI_QUICK_REFERENCE.md` - AI助手快速参考
- `DEVELOPMENT_PLAN.md` - 完整开发计划
- `openspec/project.md` - 项目技术规范

**技术文档：**
- [JOOQ官方文档](https://www.jooq.org/learn/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [H2 Database](https://www.h2database.com/html/main.html)

---

## 总结

这是一个完整的、经过验证的OpenSpec提案，包含：

- ✅ 清晰的问题说明和解决方案
- ✅ 85个详细实施任务
- ✅ 11个需求，60+个验收场景
- ✅ 完整的技术细节和代码结构
- ✅ 明确的验收标准和完成标志
- ✅ 合理的实施计划和时间估算

**状态：** 🟢 等待批准
**下一步：** 批准后开始实施

---

*本文档由AI助手生成，基于OpenSpec workflow规范*
