# MY-IMA项目 OpenSpec Workflow 指南

## 概述

本文档说明如何在MY-IMA项目中使用OpenSpec进行规范驱动的开发。OpenSpec将帮助我们系统化地规划、实现和追踪所有开发任务。

---

## OpenSpec 核心概念

### OpenSpec是什么？

OpenSpec是一个规范驱动的开发工作流框架，它通过以下方式组织开发：

- **Specs（规范）** - 定义系统当前具备的能力（已经实现的）
- **Changes（变更提案）** - 定义将要实现的功能变更（待实现的）
- **Proposals（提案）** - 详细说明为什么做这个变更、影响范围
- **Tasks（任务清单）** - 具体的实现步骤

### 工作流程

```
Stage 1: 创建变更提案
    ↓
Stage 2: 实施变更（代码实现）
    ↓
Stage 3: 归档变更（部署后更新Specs）
```

---

## 在MY-IMA项目中的应用

### 项目状态分析

根据 `DEVELOPMENT_PLAN.md`，我们当前处于：

**已实现的规范（specs/）**
- 数据库Schema设计
- Domain模型定义（15个实体）
- 基础Repository接口定义

**待实现的变更（changes/）**
- 27项开发任务，分为9个阶段

---

## 如何使用OpenSpec管理MY-IMA开发

### 决策树：何时使用OpenSpec？

```
新需求？
├─ Bug修复（恢复预期行为）→ 直接修复，无需提案
├─ 代码格式/注释/文档调整 → 直接修改，无需提案
├─ 依赖升级（非破坏性） → 直接修改，无需提案
├─ 新功能/新能力 → 创建OpenSpec提案 ✓
├─ 破坏性变更（API、Schema） → 创建OpenSpec提案 ✓
├─ 架构变更 → 创建OpenSpec提案 ✓
├─ 性能优化（影响行为） → 创建OpenSpec提案 ✓
└─ 不确定 → 创建OpenSpec提案（更安全）✓
```

### 具体到MY-IMA项目

#### 需要创建OpenSpec提案的场景

**场景1：实现Repository层**
- ✅ 需要提案：`changes/` + `specs/`
  - 这会引入新的数据访问能力
  - 涉及数据库交互逻辑
  - 需要明确定义接口契约

**场景2：实现OAuth2/OIDC协议端点**
- ✅ 需要提案：`changes/` + `specs/`
  - 新增REST API端点
  - 涉及协议标准实现
  - 需要明确的响应格式

**场景3：实现密码加密服务**
- ✅ 需要提案：`changes/` + `specs/`
  - 新增安全相关功能
  - 影响认证流程

**场景4：实现Spring Security配置**
- ✅ 需要提案：`changes/` + `specs/`
  - 架构级配置
  - 影响整体安全策略

#### 不需要提案的场景

**场景1：修复Repository实现中的bug**
- ❌ 直接修复
- 例如：修复JooqUserRepository中的编译错误
- 不改变接口契约

**场景2：优化SQL查询性能**
- ❌ 直接修改
- 例如：为查询添加索引
- 不改变功能行为

**场景3：添加单元测试**
- ❌ 直接编写
- 验证已有功能

---

## 如何为MY-IMA创建OpenSpec提案

### 示例1：创建Repository实现提案

#### Step 1: 选择change-id

选择一个唯一的、动词-led的kebab-case标识符：
```
change-id: implement-repositories
```

#### Step 2: 检查现有能力

```bash
openspec list --specs
openspec spec list --long
```

#### Step 3: 创建目录结构

```bash
mkdir -p openspec/changes/implement-repositories/specs/repository
```

#### Step 4: 编写 proposal.md

```markdown
# Change: Implement Core Repositories

## Why
Current repository interfaces are empty or partially implemented (JooqUserRepository has compilation errors). We need complete data access layer to support authentication and authorization features.

## What Changes
- Implement 6 core repositories: Realm, User, Role, Permission, Client, Token, AuditLog
- Fix JooqUserRepository compilation error
- Complete JooqUserRepository methods (findPermissions, assignRole)
- Add AuditLogRepository interface and implementation

## Impact
- **Affected specs**: repository (new spec)
- **Affected code**:
  - com.owiseman.core.jooq.repository (all interfaces)
  - com.owiseman.core.jooq.repository.impl (all implementations)
- **Dependencies**: JOOQ code generation must run first
```

#### Step 5: 编写 spec delta

在 `openspec/changes/implement-repositories/specs/repository/spec.md`:

```markdown
## ADDED Requirements

### Requirement: Realm Repository Operations
The system SHALL provide RealmRepository with CRUD operations for managing realms within a multi-tenant environment.

#### Scenario: Create realm
- **GIVEN** a valid realm configuration
- **WHEN** creating a new realm
- **THEN** realm is persisted with unique ID and enabled status set to TRUE

#### Scenario: Find realm by name
- **GIVEN** a realmId and realm name
- **WHEN** querying for realm
- **THEN** returns Optional<Realm> matching the criteria

### Requirement: User Repository Operations
The system SHALL provide UserRepository for user management with authentication and authorization support.

#### Scenario: Find user by username
- **GIVEN** a realmId and username
- **WHEN** querying for user
- **THEN** returns Optional<User> with all associated data

#### Scenario: Assign role to user
- **GIVEN** a userId and roleId
- **WHEN** assigning role to user
- **THEN** creates user-role mapping in database

#### Scenario: Find user permissions
- **GIVEN** a userId
- **WHEN** querying for permissions
- **THEN** returns list of all permission identifiers from user's roles

### Requirement: Role Repository Operations
The system SHALL provide RoleRepository for role management with permission assignment.

#### Scenario: Create role
- **GIVEN** realmId and role details
- **WHEN** creating a new role
- **THEN** role is persisted with unique ID

#### Scenario: Assign permission to role
- **GIVEN** roleId and permissionId
- **WHEN** assigning permission
- **THEN** creates role-permission mapping

### Requirement: Client Repository Operations
The system SHALL provide ClientRepository for OAuth2 client management.

#### Scenario: Authenticate client
- **GIVEN** clientId and clientSecret
- **WHEN** authenticating client
- **THEN** returns Optional<Client> if credentials valid, empty otherwise

### Requirement: Token Repository Operations
The system SHALL provide TokenRepository for JWT token lifecycle management.

#### Scenario: Create token
- **GIVEN** token details (userId, clientId, type, expiresAt)
- **WHEN** creating a token
- **THEN** token is persisted with generated ID

#### Scenario: Revoke token
- **GIVEN** tokenId
- **WHEN** revoking token
- **THEN** token is marked as invalid

### Requirement: Audit Log Repository Operations
The system SHALL provide AuditLogRepository for security event logging.

#### Scenario: Create audit log
- **GIVEN** audit event details
- **WHEN** creating an audit log
- **THEN** log entry is persisted with timestamp
```

#### Step 6: 编写 tasks.md

```markdown
## 1. Prerequisites
- [ ] 1.1 Set PGPASSWORD environment variable
- [ ] 1.2 Run `mvn jooq-codegen:generate` to generate JOOQ record classes
- [ ] 1.3 Verify generated classes in com.owiseman.core.jooq.records

## 2. Repository Implementations
- [ ] 2.1 Fix JooqUserRepository compilation error
- [ ] 2.2 Complete UserRepository methods (CRUD + findPermissions + assignRole)
- [ ] 2.3 Create RealmRepository interface and implementation
- [ ] 2.4 Create RoleRepository interface and implementation
- [ ] 2.5 Create PermissionRepository interface and implementation
- [ ] 2.6 Create PolicyRepository interface and implementation
- [ ] 2.7 Create ClientRepository interface and implementation
- [ ] 2.8 Create TokenRepository interface and implementation
- [ ] 2.9 Create AuditLogRepository interface and implementation

## 3. Testing
- [ ] 3.1 Write unit tests for each repository
- [ ] 3.2 Write integration tests with H2 database
```

#### Step 7: 验证提案

```bash
openspec validate implement-repositories --strict
```

#### Step 8: 请求批准

提案通过验证后，等待批准后再开始实施。

---

### 示例2：创建OAuth2 Token端点提案

#### Step 1: 选择change-id

```
change-id: add-oauth2-token-endpoint
```

#### Step 2: 检查现有能力

```bash
openspec list --specs
# 检查是否有oauth2相关的spec
```

#### Step 3: 创建目录

```bash
mkdir -p openspec/changes/add-oauth2-token-endpoint/specs/oauth2
```

#### Step 4: 编写 proposal.md

```markdown
# Change: Add OAuth2 Token Endpoint

## Why
We need OAuth2 token endpoint to support token issuance and refresh, which is essential for any OAuth2/OIDC compliant authentication server.

## What Changes
- Implement POST /oauth2/token endpoint
- Support grant_type: authorization_code, refresh_token, client_credentials
- Implement JWT token generation and signing
- Add TokenService for token lifecycle management

## Impact
- **Affected specs**: oauth2 (new spec)
- **Affected code**:
  - com.owiseman.controller.oauth2.TokenController
  - com.owiseman.service.TokenService
  - com.owiseman.service.JwtService
- **Dependencies**: Repository implementations must be complete first
```

#### Step 5: 编写 spec delta

```markdown
## ADDED Requirements

### Requirement: OAuth2 Token Issuance
The system SHALL provide POST /oauth2/token endpoint for issuing OAuth2 tokens according to RFC 6749.

#### Scenario: Authorization code grant
- **GIVEN** valid authorization_code, client_id, client_secret, redirect_uri
- **WHEN** requesting token with grant_type=authorization_code
- **THEN** returns access_token, refresh_token, expires_in, token_type=Bearer

#### Scenario: Refresh token grant
- **GIVEN** valid refresh_token and client credentials
- **WHEN** requesting token with grant_type=refresh_token
- **THEN** returns new access_token and refresh_token

#### Scenario: Client credentials grant
- **GIVEN** valid client_id and client_secret
- **WHEN** requesting token with grant_type=client_credentials
- **THEN** returns access_token for client-only operations

#### Scenario: Invalid grant
- **GIVEN** invalid authorization_code or expired refresh_token
- **WHEN** requesting token
- **THEN** returns HTTP 400 with error=invalid_grant

#### Scenario: Invalid client
- **GIVEN** invalid client_id or client_secret
- **WHEN** requesting token
- **THEN** returns HTTP 401 with error=invalid_client

### Requirement: JWT Token Format
The system SHALL issue JWT tokens containing user claims and proper signatures.

#### Scenario: Access token claims
- **GIVEN** authenticated user with roles and permissions
- **WHEN** generating access token
- **THEN** token includes: iss, sub, aud, exp, iat, jti, realm, username, roles, permissions

#### Scenario: Token signature
- **GIVEN** RS256 key pair configured
- **WHEN** signing JWT
- **THEN** token is signed with private key, verifiable with public key via JWKS endpoint
```

#### Step 6: 编写 tasks.md

```markdown
## 1. Prerequisites
- [ ] 1.1 Add JWT dependencies (jjwt-api, jjwt-impl, jjwt-jackson)
- [ ] 1.2 Implement JwtService (generate, validate, parse)
- [ ] 1.3 Implement PasswordService (BCrypt)
- [ ] 1.4 Implement AuthenticationService (login, logout)
- [ ] 1.5 Complete all repository implementations

## 2. Token Service
- [ ] 2.1 Create TokenService interface
- [ ] 2.2 Implement token issuance logic
- [ ] 2.3 Implement token refresh logic
- [ ] 2.4 Implement token revocation logic

## 3. Controller
- [ ] 3.1 Create TokenController
- [ ] 3.2 Implement POST /oauth2/token endpoint
- [ ] 3.3 Handle all grant types
- [ ] 3.4 Implement error responses with OAuth2 standard error codes

## 4. DTO Models
- [ ] 4.1 Create TokenRequest model
- [ ] 4.2 Create TokenResponse model
- [ ] 4.3 Create ErrorResponse model

## 5. Testing
- [ ] 5.1 Write unit tests for TokenService
- [ ] 5.2 Write integration tests for /oauth2/token endpoint
- [ ] 5.3 Test all grant types
- [ ] 5.4 Test error scenarios

## 6. Documentation
- [ ] 6.1 Update API documentation
- [ ] 6.2 Provide curl examples
```

---

## OpenSpec 三阶段工作流详解

### Stage 1: 创建变更提案

**何时使用：**
- 实现Repository层（基础数据访问）
- 实现Service层（业务逻辑）
- 实现Controller层（REST API）
- 实现OAuth2/OIDC协议
- 配置Spring Security
- 添加新的领域模型或架构变更

**检查清单：**
```bash
# 1. 查看当前状态
openspec list                           # 查看活跃的变更
openspec list --specs                   # 查看已实现的规范
openspec spec list --long               # 查看规范的详细信息

# 2. 选择唯一的change-id
# 命名规则：kebab-case, verb-led
# 例如: implement-repositories, add-oauth2-token-endpoint

# 3. 创建目录和文件
mkdir -p openspec/changes/<change-id>/specs/<capability>
touch openspec/changes/<change-id>/proposal.md
touch openspec/changes/<change-id>/tasks.md
touch openspec/changes/<change-id>/specs/<capability>/spec.md

# 4. 编写提案内容
# - proposal.md: Why, What Changes, Impact
# - tasks.md: Implementation checklist
# - spec.md: ADDED/MODIFIED/REMOVED Requirements with Scenarios

# 5. 验证提案
openspec validate <change-id> --strict

# 6. 等待批准
# 不要开始实施，直到提案被批准
```

**关键注意事项：**
- 每个 `## ADDED Requirements` 下必须有至少一个 `#### Scenario:`
- Scenario格式必须是 `#### Scenario: 名称`（4个#）
- 每个Scenario使用 `- **GIVEN**` / `- **WHEN**` / `- **THEN**` 格式
- 使用 `## ADDED` 添加新能力，`## MODIFIED` 修改已有能力
- `## MODIFIED` 时必须复制完整的requirement文本（包括所有scenarios）

---

### Stage 2: 实施变更

**何时开始：** 提案被批准后

**实施步骤：**

```bash
# 1. 读取提案文档
cat openspec/changes/<change-id>/proposal.md
cat openspec/changes/<change-id>/tasks.md
cat openspec/changes/<change-id>/design.md  # 如果存在

# 2. 按顺序完成tasks.md中的任务
# - 完成一项，勾选一项
# - 严格遵守顺序，依赖关系

# 3. 创建TODO清单跟踪进度（使用AI的todowrite工具）
# 例如：
todowrite --todos '[
  {"id": "1", "content": "运行JOOQ代码生成", "status": "in_progress"},
  {"id": "2", "content": "实现UserRepository", "status": "pending"},
  ...
]'

# 4. 完成所有tasks后，更新tasks.md确保所有项都已勾选

# 5. Approval Gate: 在开始下一个变更前，确认当前变更已完成
```

**实施最佳实践：**
- 每完成一个task就标记为完成
- 遵循项目中已定义的代码风格（参考 openspec/project.md）
- Repository层使用JOOQ的类型安全查询
- Service层使用清晰的业务逻辑
- Controller层返回统一的响应格式
- 所有新增代码必须有对应的单元测试

---

### Stage 3: 归档变更

**何时进行：** 变更已部署到生产环境

**归档步骤：**

```bash
# 1. 确认变更已部署到生产环境

# 2. 归档变更（这会更新specs/目录）
openspec archive <change-id> --yes

# 或对于工具-only变更（不修改specs）:
openspec archive <change-id> --skip-specs --yes

# 3. 验证归档后的状态
openspec validate --strict

# 4. 确认specs/已更新
ls openspec/specs/
```

**归档的作用：**
- 将 `changes/<change-id>/` 移动到 `changes/archive/YYYY-MM-DD-<change-id>/`
- 更新 `specs/<capability>/spec.md` 反映当前系统的真实状态
- 保持 `changes/` 目录只包含未完成的变更

---

## 在MY-IMA项目中的具体工作模式

### 为AI助手的工作流程

当你向AI助手请求实现某个功能时，AI助手应该：

#### 1. 判断是否需要OpenSpec提案

```python
if request in [bug_fix, format_change, doc_update, dependency_update]:
    直接实现
else:
    # 检查是否已有相关提案
    if existing_proposal_for_capability:
        检查提案是否已完成
        if not_completed:
            继续实现提案中的tasks
        else:
            提示需要创建新提案
    else:
        创建新的OpenSpec提案
```

#### 2. 实施时的检查清单

**在实现任何代码之前，AI助手应该：**
```bash
# 1. 读取openspec/project.md了解项目规范
# 2. 运行openspec list查看是否有未完成的变更
# 3. 运行openspec spec list --long查看已有规范
# 4. 如果是提案中的任务，读取tasks.md了解具体步骤
```

**在实现代码时，AI助手应该：**
```bash
# 1. 使用todowrite跟踪进度
# 2. 遵循openspec/project.md中的代码风格
# 3. 完成后更新tasks.md
# 4. 编写测试验证实现
```

---

## 针对DEVELOPMENT_PLAN.md的OpenSpec使用策略

### 阶段1-2（基础设施和核心服务）- 使用OpenSpec

**建议的变更提案：**

| 变更ID | 描述 | Capability |
|--------|------|------------|
| `implement-repositories` | 实现所有Repository | repository |
| `implement-password-service` | 实现密码加密服务 | password-service |
| `implement-jwt-service` | 实现JWT服务 | jwt-service |
| `implement-authentication-service` | 实现认证服务 | authentication |

**工作流：**
1. 创建 `implement-repositories` 提案
2. 提案批准后，开始实现
3. 完成后归档
4. 创建下一个提案...

### 阶段3-4（OAuth2和管理API）- 使用OpenSpec

**建议的变更提案：**

| 变更ID | 描述 | Capability |
|--------|------|------------|
| `add-oauth2-token-endpoint` | OAuth2 Token端点 | oauth2 |
| `add-oauth2-authorization-endpoint` | OAuth2授权码流程 | oauth2 |
| `add-userinfo-endpoint` | UserInfo端点 | oauth2 |
| `add-jwks-endpoint` | JWKS端点 | oauth2 |
| `add-realm-management-api` | Realm管理API | realm-api |
| `add-user-management-api` | 用户管理API | user-api |

### 阶段5-6（Security配置）- 使用OpenSpec

**建议的变更提案：**

| 变更ID | 描述 | Capability |
|--------|------|------------|
| `configure-spring-security` | Spring Security配置 | security |
| `add-error-handling` | 统一错误处理 | error-handling |

### 阶段7-9（测试、监控、文档）- 可以直接实现

这些阶段可以不使用OpenSpec，因为它们是支持性工作：

- 单元测试和集成测试
- 健康检查端点
- API文档

---

## 常见问题

### Q1: 什么时候不需要创建OpenSpec提案？

**直接实现（无需提案）：**
- 修复bug（恢复预期行为）
- 代码格式调整、添加注释
- 添加单元测试
- 性能优化（不改变行为）
- 依赖升级（非破坏性）

### Q2: 如何判断一个任务是bug还是新功能？

**Bug的判断标准：**
- 代码没有按照spec中定义的行为工作
- 测试用例失败，但spec没有改变

**新功能的判断标准：**
- 引入了新的能力或端点
- 改变了已有的接口契约
- 需要修改database schema
- 影响多个模块或服务

### Q3: 提案中的tasks.md可以修改吗？

**可以，但需要注意：**
- 如果只是增加更细粒度的步骤，直接添加即可
- 如果改变了任务范围，需要更新proposal.md的Impact部分
- 如果删除了某些任务，确保它们确实不需要

### Q4: 实施过程中发现需要修改提案怎么办？

**处理流程：**
1. 停止实施
2. 修改proposal.md、spec.md、tasks.md
3. 重新运行 `openspec validate <change-id> --strict`
4. 请求重新批准
5. 批准后继续实施

### Q5: 多个AI助手如何协作？

**协作策略：**
1. 在开始任何工作前，运行 `openspec list` 查看是否有未完成的变更
2. 如果有未完成的变更，检查是否与你的工作冲突
3. 如果冲突，协调分工或合并提案
4. 如果不冲突，可以在不同的change下并行工作

---

## 实际操作示例

### 示例：AI助手被要求"实现UserRepository"

**AI助手的思维过程：**

```
1. 分析请求
   - 请求：实现UserRepository
   - 类型：实现新的数据访问能力
   - 决策：需要OpenSpec提案

2. 检查现有状态
   运行: openspec list
   结果: 发现 "implement-repositories" 变更已经存在

3. 检查变更详情
   运行: openspec show implement-repositories
   发现: tasks.md中有 "2.2 Complete UserRepository methods"

4. 决策
   - 已经有提案，不需要创建新提案
   - 可以直接实施任务

5. 实施步骤
   - 读取tasks.md了解具体步骤
   - 读取proposal.md了解上下文
   - 使用todowrite跟踪进度
   - 按步骤实现
   - 更新tasks.md

6. 完成后
   - 确认所有tasks已勾选
   - 运行测试验证
   - 等待归档（部署后）
```

---

## 总结

在MY-IMA项目中使用OpenSpec的核心原则：

1. **大功能用OpenSpec，小改动直接实现**
   - Repository、Service、Controller层实现 → OpenSpec
   - Bug修复、格式调整 → 直接实现

2. **先提案，后实施**
   - 创建proposal.md → 验证 → 批准 → 实施 → 归档

3. **保持spec和代码同步**
   - specs/ 反映系统当前状态
   - changes/ 反映计划中的状态

4. **遵循项目规范**
   - openspec/project.md 定义代码风格
   - 提案中的spec.md 定义行为规范

5. **使用OpenSpec CLI工具**
   - `openspec list` - 查看进度
   - `openspec validate` - 验证提案
   - `openspec archive` - 归档完成的工作

通过这个工作流，我们可以系统化地完成MY-IMA项目的27个开发任务，确保每一步都有清晰的规范和可追溯的记录。
