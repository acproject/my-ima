# MY-IMA 开发计划

## 项目概述
对标Keycloak的轻量级、可嵌入、可扩展的IAM/Auth Server，提供多租户认证授权、OAuth2/OIDC协议支持。

## OpenSpec Workflow 集成

本项目使用 **OpenSpec** 进行规范驱动的开发。所有变更都遵循以下原则：

### 快速决策

```
需要OpenSpec提案 ✓
- 新功能/新能力（Repository、Service、Controller）
- API端点变更（OAuth2/OIDC）
- 数据库Schema变更
- 架构变更（Security配置）

直接实现 ✗
- Bug修复（恢复预期行为）
- 代码格式/注释/文档
- 依赖升级（非破坏性）
- 添加单元测试
- 性能优化（不改变行为）
```

### OpenSpec工作流

```
Stage 1: 创建提案
    ↓ 检查清单:
    - 运行 `openspec list` 查看现有变更
    - 运行 `openspec spec list --long` 查看现有规范
    - 创建 proposal.md, tasks.md, spec.md
    - 运行 `openspec validate <change-id> --strict`
    - 等待批准

Stage 2: 实施变更
    ↓ 实施清单:
    - 读取 tasks.md 了解具体步骤
    - 使用 todowrite 跟踪进度
    - 按步骤实现并更新 tasks.md
    - 编写测试验证实现

Stage 3: 归档变更
    ↓ 归档清单:
    - 确认变更已部署到生产环境
    - 运行 `openspec archive <change-id> --yes`
    - 运行 `openspec validate --strict` 验证
```

**参考文档：**
- 完整指南：`OPENSPEC_WORKFLOW_GUIDE.md`
- 快速参考：`AI_QUICK_REFERENCE.md`
- 项目规范：`openspec/project.md`

---

## 当前状态

### 已完成 ✓
- [x] Spring Boot脚手架搭建
- [x] 数据库Schema设计（PostgreSQL）
- [x] Domain模型定义（15个实体类）
- [x] 枚举类型定义（5个enum）
- [x] Repository接口定义（6个接口）
- [x] JOOQ Maven插件配置
- [x] 应用配置文件

### 未完成 ✗
- [ ] JOOQ代码生成
- [ ] Repository实现
- [ ] Service层（业务逻辑）
- [ ] Controller层（REST API）
- [ ] Spring Security配置
- [ ] 密码加密和JWT服务
- [ ] OAuth2/OIDC协议实现
- [ ] 测试用例

---

## 开发计划（按优先级分阶段）

### 📊 变更提案总览

| 变更ID | 描述 | Capability | 优先级 | 依赖 |
|--------|------|------------|--------|------|
| `run-jooq-codegen` | JOOQ代码生成 | - | HIGH | 无 |
| `implement-repositories` | 实现所有Repository | repository | HIGH | run-jooq-codegen |
| `implement-password-service` | 密码加密服务 | password-service | HIGH | implement-repositories |
| `implement-jwt-service` | JWT生成和验证 | jwt-service | HIGH | 无 |
| `implement-authentication-service` | 认证服务 | authentication | HIGH | password-service, jwt-service |
| `implement-authorization-service` | 授权服务 | authorization | MEDIUM | implement-repositories |
| `implement-audit-service` | 审计日志服务 | audit-service | MEDIUM | implement-repositories |
| `add-oauth2-token-endpoint` | OAuth2 Token端点 | oauth2 | HIGH | authentication-service |
| `add-oauth2-authorization-endpoint` | OAuth2授权码流程 | oauth2 | HIGH | oauth2-token-endpoint |
| `add-userinfo-endpoint` | UserInfo端点 | oauth2 | HIGH | oauth2-token-endpoint |
| `add-jwks-endpoint` | JWKS端点 | oauth2 | HIGH | jwt-service |
| `add-oidc-discovery-endpoint` | OIDC发现端点 | oauth2 | MEDIUM | oauth2-token-endpoint |
| `add-realm-management-api` | Realm管理API | realm-api | MEDIUM | implement-repositories |
| `add-user-management-api` | 用户管理API | user-api | MEDIUM | implement-repositories, authentication-service |
| `add-role-management-api` | 角色管理API | role-api | MEDIUM | implement-repositories |
| `add-permission-management-api` | 权限管理API | permission-api | MEDIUM | implement-repositories |
| `add-client-management-api` | 客户端管理API | client-api | MEDIUM | implement-repositories |
| `add-audit-log-api` | 审计日志API | audit-api | LOW | audit-service |
| `configure-spring-security` | Spring Security配置 | security | HIGH | oauth2-token-endpoint |
| `configure-cors-csrf` | CORS和CSRF配置 | security | MEDIUM | configure-spring-security |
| `add-error-handling` | 统一错误处理 | error-handling | MEDIUM | 无 |

---

### 第一阶段：基础设施层（必须优先完成）

**OpenSpec策略：** 此阶段的所有任务都需要OpenSpec提案

#### 1.1 数据库层

**变更ID：** `run-jooq-codegen`
**OpenSpec：** 不需要（工具配置任务，直接实现）

- [ ] **JOOQ代码生成**
  - [ ] 配置环境变量 `PGPASSWORD`
  - [ ] 运行 `mvn jooq-codegen:generate`
  - [ ] 验证生成的记录类位于 `com.owiseman.core.jooq.records`
  - [ ] 确认所有表都已生成对应的Record类

**验证步骤：**
```bash
export PGPASSWORD=your_password
mvn jooq-codegen:generate
ls src/main/java/com/owiseman/core/jooq/records/
```

#### 1.2 Repository实现（数据访问层）

**变更ID：** `implement-repositories`
**OpenSpec：** 需要 ✓
**提案位置：** `openspec/changes/implement-repositories/`
**依赖：** `run-jooq-codegen`

**实施前检查清单：**
```bash
# 1. 检查是否有未完成的变更
openspec list

# 2. 查看是否已存在提案
openspec show implement-repositories

# 3. 如果不存在，需要创建提案
# 参考: OPENSPEC_WORKFLOW_GUIDE.md 的示例1
```

**提案验证：**
```bash
openspec validate implement-repositories --strict
```

**实施步骤：**
- [ ] **RealmRepository** (创建接口 + 实现)
  - [ ] 创建 `RealmRepository.java` 接口
  - [ ] 创建 `JooqRealmRepository.java` 实现
  - [ ] `findById(UUID id)`
  - [ ] `findByName(UUID realmId, String name)`
  - [ ] `create(Realm realm)`
  - [ ] `update(Realm realm)`
  - [ ] `enable(UUID id)` / `disable(UUID id)`
  - [ ] 编写单元测试

- [ ] **UserRepository** (完善接口 + 实现)
  - `findById(UUID id)`
  - `findByName(UUID realmId, String name)`
  - `create(Realm realm)`
  - `update(Realm realm)`
  - `enable(UUID id)` / `disable(UUID id)`

- [ ] **UserRepository** (完善接口 + 实现)
  - [ ] 修复 `JooqUserRepository.java` 编译错误
  - [ ] 完成 `findByUsername(UUID realmId, String username)` - 使用JOOQ实现
  - [ ] 实现 `findPermissions(UUID userId)` - 通过关联表查询权限
  - [ ] 实现 `assignRole(UUID userId, String role)` - 插入user_role表
  - [ ] 添加 `create(User user)` / `update(User user)`
  - [ ] 添加 `findById(UUID id)` / `findAll(UUID realmId)`
  - [ ] 添加 `delete(UUID id)` / `findByEmail(UUID realmId, String email)`
  - [ ] 编写单元测试

- [ ] **RoleRepository** (创建接口 + 实现)
  - [ ] 创建 `RoleRepository.java` 接口
  - [ ] 创建 `JooqRoleRepository.java` 实现
  - [ ] `findById(UUID id)`
  - [ ] `findByName(UUID realmId, String name)`
  - [ ] `create(Role role)` / `update(Role role)`
  - [ ] `delete(UUID id)`
  - [ ] `assignPermission(UUID roleId, UUID permissionId)`
  - [ ] `removePermission(UUID roleId, UUID permissionId)`
  - [ ] `findPermissions(UUID roleId)`
  - [ ] `findUserRoles(UUID userId)`
  - [ ] 编写单元测试

- [ ] **PermissionRepository** (创建接口 + 实现)
  - [ ] 创建 `PermissionRepository.java` 接口
  - [ ] 创建 `JooqPermissionRepository.java` 实现
  - [ ] `findById(UUID id)`
  - [ ] `findByResourceAction(UUID realmId, String resource, String action)`
  - [ ] `create(Permission permission)` / `update(Permission permission)`
  - [ ] `delete(UUID id)`
  - [ ] `findByPolicy(UUID policyId)`
  - [ ] `assignPolicy(UUID permissionId, UUID policyId)`
  - [ ] `findAll(UUID realmId)`
  - [ ] 编写单元测试

- [ ] **PolicyRepository** (创建接口 + 实现)
  - [ ] 创建 `PolicyRepository.java` 接口
  - [ ] 创建 `JooqPolicyRepository.java` 实现
  - [ ] `findById(UUID id)`
  - [ ] `create(Policy policy)` / `update(Policy policy)`
  - [ ] `delete(UUID id)`
  - [ ] `findByType(UUID realmId, PolicyType type)`
  - [ ] `findAll(UUID realmId)`
  - [ ] `evaluate(UUID policyId, Map<String, Object> context)` - 表达式求值
  - [ ] 编写单元测试

- [ ] **ClientRepository** (创建接口 + 实现)
  - [ ] 创建 `ClientRepository.java` 接口
  - [ ] 创建 `JooqClientRepository.java` 实现
  - [ ] `findById(UUID id)`
  - [ ] `findByClientId(UUID realmId, String clientId)`
  - [ ] `create(Client client)` / `update(Client client)`
  - [ ] `delete(UUID id)`
  - [ ] `authenticate(String clientId, String clientSecret)` - 密码验证
  - [ ] `findAll(UUID realmId)`
  - [ ] 编写单元测试

- [ ] **TokenRepository** (创建接口 + 实现)
  - [ ] 创建 `TokenRepository.java` 接口
  - [ ] 创建 `JooqTokenRepository.java` 实现
  - [ ] `create(Token token)`
  - [ ] `findById(UUID id)`
  - [ ] `findByUserId(UUID userId, TokenType type)`
  - [ ] `findByClientId(UUID clientId, TokenType type)`
  - [ ] `revoke(UUID id)` / `revokeByUser(UUID userId)`
  - [ ] `deleteExpired()` - 清理过期token
  - [ ] 编写单元测试

- [ ] **AuditLogRepository** (创建接口 + 实现)
  - [ ] 创建 `AuditLogRepository.java` 接口
  - [ ] 创建 `JooqAuditLogRepository.java` 实现
  - [ ] `create(AuditLog log)`
  - [ ] `findById(Long id)`
  - [ ] `findByUser(UUID userId, LocalDateTime start, LocalDateTime end)`
  - [ ] `findByEvent(AuditEventType type, LocalDateTime start, LocalDateTime end)`
  - [ ] `findAll(UUID realmId, LocalDateTime start, LocalDateTime end)`
  - [ ] 编写单元测试

**完成后检查清单：**
```bash
# 1. 确认所有tasks已勾选
cat openspec/changes/implement-repositories/tasks.md

# 2. 运行测试
mvn test

# 3. 验证编译
mvn clean compile

# 4. 等待归档（部署后）
# openspec archive implement-repositories --yes
```

### 第二阶段：核心服务层

**OpenSpec策略：** 此阶段的所有Service实现都需要OpenSpec提案

---

#### 2.1 密码服务

**变更ID：** `implement-password-service`
**OpenSpec：** 需要 ✓
**Capability：** `password-service`
**提案位置：** `openspec/changes/implement-password-service/`

**实施前检查清单：**
```bash
# 1. 检查是否已有提案
openspec show implement-password-service

# 2. 如果没有，参考模板创建提案
# - proposal.md (Why, What, Impact)
# - tasks.md (Implementation checklist)
# - specs/password-service/spec.md (ADDED Requirements)

# 3. 验证提案
openspec validate implement-password-service --strict
```

**实施步骤：**
- [ ] **PasswordService** (接口 + 实现)
  - [ ] 创建 `PasswordService.java` 接口
  - [ ] 创建 `PasswordServiceImpl.java` 实现
  - [ ] `encode(String rawPassword)` - BCrypt加密
  - [ ] `matches(String rawPassword, String encodedPassword)` - 密码验证
  - [ ] `validateStrength(String password)` - 密码强度检查
  - [ ] 添加配置类（BCryptPasswordEncoder bean）
  - [ ] 编写单元测试

**完成后检查：**
```bash
# 验证提案tasks.md所有项已勾选
cat openspec/changes/implement-password-service/tasks.md

# 运行测试
mvn test -Dtest=PasswordServiceTest
```

#### 2.2 JWT服务

**变更ID：** `implement-jwt-service`
**OpenSpec：** 需要 ✓
**Capability：** `jwt-service`

**实施步骤：**
- [ ] **JwtService** (接口 + 实现)
  - [ ] 添加JWT依赖（jjwt-api, jjwt-impl, jjwt-jackson）
  - [ ] 创建 `JwtService.java` 接口
  - [ ] 创建 `JwtServiceImpl.java` 实现
  - [ ] `generateAccessToken(TokenClaims claims)` - 生成access token
  - [ ] `generateRefreshToken(TokenClaims claims)` - 生成refresh token
  - [ ] `validateToken(String token)` - 验证token签名和过期时间
  - [ ] `parseToken(String token)` - 解析token返回claims
  - [ ] `getJwks()` - 返回JWK Set
  - [ ] 配置RSA密钥对生成或加载
  - [ ] 配置token过期时间（access: 1小时, refresh: 30天）
  - [ ] 编写单元测试

#### 2.3 认证授权服务

**变更ID：** `implement-authentication-service`
**OpenSpec：** 需要 ✓
**Capability：** `authentication`
**依赖：** `implement-password-service`, `implement-jwt-service`

**实施步骤：**
- [ ] **AuthenticationService** (接口 + 实现)
  - [ ] 创建 `AuthenticationService.java` 接口
  - [ ] 创建 `AuthenticationServiceImpl.java` 实现
  - [ ] `login(UUID realmId, String username, String password, String clientId)` - 用户登录
    - [ ] 验证realm存在且启用
    - [ ] 验证user存在且启用
    - [ ] 验证密码
    - [ ] 生成JWT tokens
    - [ ] 记录audit log
  - [ ] `logout(String token)` - 用户登出
    - [ ] 验证token
    - [ ] 撤销token（token黑名单或标记过期）
    - [ ] 记录audit log
  - [ ] `refreshToken(String refreshToken)` - 刷新token
    - [ ] 验证refresh token
    - [ ] 生成新的access token和refresh token
    - [ ] 撤销旧的refresh token
  - [ ] `validateToken(String token)` - 验证token有效性
  - [ ] 编写单元测试
  - [ ] 编写集成测试

#### 2.4 授权服务

**变更ID：** `implement-authorization-service`
**OpenSpec：** 需要 ✓
**Capability：** `authorization`
**依赖：** `implement-repositories`

**实施步骤：**
- [ ] **AuthorizationService** (接口 + 实现)
  - [ ] 创建 `AuthorizationService.java` 接口
  - [ ] 创建 `AuthorizationServiceImpl.java` 实现
  - [ ] `checkPermission(UUID userId, String resource, String action)` - 权限检查
    - [ ] 查询用户所有角色
    - [ ] 查询角色所有权限
    - [ ] 检查是否匹配resource和action
  - [ ] `checkPolicy(UUID userId, UUID policyId)` - 策略评估
  - [ ] `evaluatePolicies(UUID userId, List<UUID> policyIds)` - 批量策略评估
  - [ ] `getUserPermissions(UUID userId)` - 获取用户所有权限
  - [ ] 实现策略表达式求值（可选：引入SpEL或自定义引擎）
  - [ ] 编写单元测试

#### 2.5 审计服务

**变更ID：** `implement-audit-service`
**OpenSpec：** 需要 ✓
**Capability：** `audit-service`
**依赖：** `implement-repositories`

**实施步骤：**
- [ ] **AuditService** (接口 + 实现)
  - [ ] 创建 `AuditService.java` 接口
  - [ ] 创建 `AuditServiceImpl.java` 实现
  - [ ] `logLogin(UUID realmId, UUID userId, String ip)` - 记录登录
  - [ ] `logLogout(UUID realmId, UUID userId, String ip)` - 记录登出
  - [ ] `logPasswordChange(UUID realmId, UUID userId)` - 记录密码变更
  - [ ] `logRoleChange(UUID realmId, UUID userId, List<String> oldRoles, List<String> newRoles)` - 记录角色变更
  - [ ] 配置异步日志记录（使用@Async）
  - [ ] 编写单元测试

#### 2.1 安全服务
- [ ] **PasswordService**
  - `encode(String rawPassword)` - BCrypt加密
  - `matches(String rawPassword, String encodedPassword)` - 密码验证
  - `validateStrength(String password)` - 密码强度检查

- [ ] **JwtService**
  - `generateAccessToken(TokenClaims claims)` - 生成access token
  - `generateRefreshToken(TokenClaims claims)` - 生成refresh token
  - `validateToken(String token)` - 验证token签名和过期时间
  - `parseToken(String token)` - 解析token返回claims
  - `getJwks()` - 返回JWK Set

#### 2.2 认证授权服务
- [ ] **AuthenticationService**
  - `login(UUID realmId, String username, String password, String clientId)` - 用户登录
  - `logout(String token)` - 用户登出
  - `refreshToken(String refreshToken)` - 刷新token
  - `validateToken(String token)` - 验证token有效性

- [ ] **AuthorizationService**
  - `checkPermission(UUID userId, String resource, String action)` - 权限检查
  - `checkPolicy(UUID userId, UUID policyId)` - 策略评估
  - `evaluatePolicies(UUID userId, List<UUID> policyIds)` - 批量策略评估
  - `getUserPermissions(UUID userId)` - 获取用户所有权限

- [ ] **AuditService**
  - `logLogin(UUID realmId, UUID userId, String ip)` - 记录登录
  - `logLogout(UUID realmId, UUID userId, String ip)` - 记录登出
  - `logPasswordChange(UUID realmId, UUID userId)` - 记录密码变更
  - `logRoleChange(UUID realmId, UUID userId, List<String> oldRoles, List<String> newRoles)` - 记录角色变更

### 第三阶段：OAuth2/OIDC协议实现

**OpenSpec策略：** 此阶段的所有端点实现都需要OpenSpec提案

---

#### 3.1 OAuth2 Token端点

**变更ID：** `add-oauth2-token-endpoint`
**OpenSpec：** 需要 ✓
**Capability：** `oauth2`
**依赖：** `implement-authentication-service`

**实施前检查清单：**
```bash
# 1. 检查认证服务是否完成
openspec show implement-authentication-service

# 2. 检查是否有oauth2相关的spec
openspec spec list --long | grep oauth2

# 3. 创建提案并验证
openspec validate add-oauth2-token-endpoint --strict
```

**实施步骤：**
- [ ] **TokenController**
  - [ ] 创建 `TokenController.java`
  - [ ] 创建DTO模型：
    - [ ] `TokenRequest.java` (grant_type, code, refresh_token, client_id, client_secret, etc.)
    - [ ] `TokenResponse.java` (access_token, refresh_token, token_type, expires_in)
  - [ ] `POST /oauth2/token` - Token端点
    - [ ] `grant_type=authorization_code` - 授权码换取token
      - [ ] 验证authorization_code有效性
      - [ ] 验证client_id和client_secret
      - [ ] 验证redirect_uri
      - [ ] 生成access_token和refresh_token
      - [ ] 返回标准OAuth2响应
    - [ ] `grant_type=refresh_token` - 刷新token
      - [ ] 验证refresh_token
      - [ ] 撤销旧的refresh_token
      - [ ] 生成新的token对
    - [ ] `grant_type=client_credentials` - 客户端凭证模式
      - [ ] 验证client_id和client_secret
      - [ ] 生成access_token（无refresh_token）
    - [ ] `grant_type=password` - 密码模式（不推荐，可选）
  - [ ] 实现OAuth2标准错误响应
    - [ ] invalid_request (400)
    - [ ] invalid_client (401)
    - [ ] invalid_grant (400)
    - [ ] unauthorized_client (401)
    - [ ] unsupported_grant_type (400)
  - [ ] 编写单元测试
  - [ ] 编写集成测试（使用Postman或curl）

**测试示例：**
```bash
# Authorization Code Grant
curl -X POST http://localhost:8086/oauth2/token \
  -d "grant_type=authorization_code" \
  -d "code=SplxlOBeZQQYbYS6WxSbIA" \
  -d "redirect_uri=https://example.com/callback" \
  -d "client_id=myclient" \
  -d "client_secret=secret"

# Refresh Token Grant
curl -X POST http://localhost:8086/oauth2/token \
  -d "grant_type=refresh_token" \
  -d "refresh_token=tGzv3JOkF0XG5Qx2TlKWIA" \
  -d "client_id=myclient" \
  -d "client_secret=secret"
```

#### 3.2 OAuth2授权码流程

**变更ID：** `add-oauth2-authorization-endpoint`
**OpenSpec：** 需要 ✓
**Capability：** `oauth2`
**依赖：** `add-oauth2-token-endpoint`

**实施步骤：**
- [ ] **AuthorizationController**
  - [ ] 创建 `AuthorizationController.java`
  - [ ] 创建DTO模型：
    - [ ] `AuthorizationRequest.java` (response_type, client_id, redirect_uri, scope, state)
  - [ ] `GET /oauth2/authorize` - 授权端点
    - [ ] 验证client_id和redirect_uri
    - [ ] 验证scope
    - [ ] 检查用户登录状态
    - [ ] 展示授权页面（scope、权限说明）
    - [ ] 用户授权后，生成authorization_code
    - [ ] 重定向到redirect_uri?code=xxx&state=yyy
  - [ ] 创建授权页面模板（Thymeleaf）
  - [ ] 实现authorization_code存储（临时，5分钟过期）
  - [ ] 编写集成测试

#### 3.3 UserInfo端点

**变更ID：** `add-userinfo-endpoint`
**OpenSpec：** 需要 ✓
**Capability：** `oauth2`
**依赖：** `add-oauth2-token-endpoint`

**实施步骤：**
- [ ] **UserInfoController**
  - [ ] 创建 `UserInfoController.java`
  - [ ] `GET /oauth2/userinfo` - UserInfo端点（RFC 7662）
    - [ ] 验证access_token（Bearer token）
    - [ ] 从token中解析用户信息
    - [ ] 返回用户信息（sub, name, email, groups等）
    - [ ] 支持scope限制返回字段
  - [ ] 创建 `UserInfoResponse.java` DTO
  - [ ] 编写集成测试

**测试示例：**
```bash
curl -X GET http://localhost:8086/oauth2/userinfo \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 3.4 JWKS端点

**变更ID：** `add-jwks-endpoint`
**OpenSpec：** 需要 ✓
**Capability：** `oauth2`
**依赖：** `implement-jwt-service`

**实施步骤：**
- [ ] **JwksController**
  - [ ] 创建 `JwksController.java`
  - [ ] `GET /.well-known/jwks.json` - JWKS端点（RFC 7517）
    - [ ] 返回JWT签名公钥集（JSON Web Key Set）
    - [ ] 支持多密钥轮换（kid）
  - [ ] 创建 `JwkResponse.java` DTO
  - [ ] 集成JwtService的getJwks()方法
  - [ ] 编写单元测试

**测试示例：**
```bash
curl -X GET http://localhost:8086/.well-known/jwks.json
```

#### 3.5 OIDC发现端点

**变更ID：** `add-oidc-discovery-endpoint`
**OpenSpec：** 需要 ✓
**Capability：** `oauth2`
**依赖：** `add-oauth2-token-endpoint`, `add-userinfo-endpoint`, `add-jwks-endpoint`

**实施步骤：**
- [ ] **DiscoveryController**
  - [ ] 创建 `DiscoveryController.java`
  - [ ] `GET /.well-known/openid-configuration` - OIDC发现端点
    - [ ] 返回OIDC配置信息（issuer, authorization_endpoint, token_endpoint, userinfo_endpoint, jwks_uri, scopes_supported, etc.）
  - [ ] 创建 `OpenIDConfiguration.java` DTO
  - [ ] 配置issuer URL
  - [ ] 编写单元测试

**测试示例：**
```bash
curl -X GET http://localhost:8086/.well-known/openid-configuration
```

#### 3.1 OAuth2授权流程
- [ ] **Authorization Controller**
  - `GET /oauth2/authorize` - 授权端点（授权码流程）
    - 验证client_id、redirect_uri、scope
    - 检查用户登录状态
    - 展示授权页面（scope、权限说明）
    - 生成authorization_code并重定向

- [ ] **Token Controller**
  - `POST /oauth2/token` - Token端点
    - `grant_type=authorization_code` - 授权码换取token
    - `grant_type=refresh_token` - 刷新token
    - `grant_type=client_credentials` - 客户端凭证模式
    - `grant_type=password` - 密码模式（不推荐）
    - 返回access_token、refresh_token、expires_in

- [ ] **UserInfo Controller**
  - `GET /oauth2/userinfo` - UserInfo端点（RFC 7662）
    - 验证access_token
    - 返回用户信息（sub, name, email, groups等）

- [ ] **JWKS Controller**
  - `GET /.well-known/jwks.json` - JWKS端点（RFC 7517）
    - 返回JWT签名公钥集

- [ ] **Discovery Controller**
  - `GET /.well-known/openid-configuration` - OIDC发现端点
    - 返回OIDC配置信息

### 第四阶段：管理API

**OpenSpec策略：** 此阶段的所有管理API都需要OpenSpec提案

---

#### 4.1 Realm管理API

**变更ID：** `add-realm-management-api`
**OpenSpec：** 需要 ✓
**Capability：** `realm-api`
**依赖：** `implement-repositories`

**实施步骤：**
- [ ] **RealmController**
  - [ ] 创建 `RealmController.java`
  - [ ] 创建DTO模型：
    - [ ] `RealmRequest.java`
    - [ ] `RealmResponse.java`
  - [ ] `POST /realms` - 创建realm
    - [ ] 验证request body
    - [ ] 检查realm名称唯一性
    - [ ] 调用Repository创建
    - [ ] 返回创建的realm
  - [ ] `GET /realms/{id}` - 查询realm
  - [ ] `GET /realms` - 列表查询（分页、排序）
  - [ ] `PUT /realms/{id}` - 更新realm
  - [ ] `DELETE /realms/{id}` - 删除realm（软删除）
  - [ ] `POST /realms/{id}/enable` - 启用
  - [ ] `POST /realms/{id}/disable` - 禁用
  - [ ] 添加权限验证（需要admin角色）
  - [ ] 编写单元测试
  - [ ] 编写集成测试

#### 4.2 用户管理API

**变更ID：** `add-user-management-api`
**OpenSpec：** 需要 ✓
**Capability：** `user-api`
**依赖：** `implement-repositories`, `implement-authentication-service`

**实施步骤：**
- [ ] **UserController**
  - [ ] 创建 `UserController.java`
  - [ ] 创建DTO模型：
    - [ ] `UserRequest.java`
    - [ ] `UserResponse.java`
    - [ ] `PasswordChangeRequest.java`
  - [ ] `POST /realms/{realmId}/users` - 创建用户
    - [ ] 验证request body
    - [ ] 密码加密
    - [ ] 调用Repository创建
  - [ ] `GET /realms/{realmId}/users/{id}` - 查询用户
  - [ ] `GET /realms/{realmId}/users` - 用户列表（分页、搜索）
  - [ ] `PUT /realms/{realmId}/users/{id}` - 更新用户
  - [ ] `DELETE /realms/{realmId}/users/{id}` - 删除用户
  - [ ] `POST /realms/{realmId}/users/{id}/password` - 修改密码
    - [ ] 验证旧密码
    - [ ] 加密新密码
    - [ ] 记录audit log
  - [ ] `POST /realms/{realmId}/users/{id}/roles` - 分配角色
  - [ ] `DELETE /realms/{realmId}/users/{id}/roles/{roleId}` - 移除角色
  - [ ] `GET /realms/{realmId}/users/{id}/roles` - 查询用户角色
  - [ ] `GET /realms/{realmId}/users/{id}/permissions` - 查询用户权限
  - [ ] 添加权限验证
  - [ ] 编写测试

#### 4.3 角色管理API

**变更ID：** `add-role-management-api`
**OpenSpec：** 需要 ✓
**Capability：** `role-api`
**依赖：** `implement-repositories`

**实施步骤：**
- [ ] **RoleController**
  - [ ] 创建 `RoleController.java`
  - [ ] 创建DTO模型：
    - [ ] `RoleRequest.java`
    - [ ] `RoleResponse.java`
  - [ ] `POST /realms/{realmId}/roles` - 创建角色
  - [ ] `GET /realms/{realmId}/roles/{id}` - 查询角色
  - [ ] `GET /realms/{realmId}/roles` - 角色列表（分页）
  - [ ] `PUT /realms/{realmId}/roles/{id}` - 更新角色
  - [ ] `DELETE /realms/{realmId}/roles/{id}` - 删除角色
  - [ ] `POST /realms/{realmId}/roles/{id}/permissions` - 分配权限
  - [ ] `DELETE /realms/{realmId}/roles/{id}/permissions/{permissionId}` - 移除权限
  - [ ] `GET /realms/{realmId}/roles/{id}/permissions` - 查询角色权限
  - [ ] 添加权限验证
  - [ ] 编写测试

#### 4.4 权限管理API

**变更ID：** `add-permission-management-api`
**OpenSpec：** 需要 ✓
**Capability：** `permission-api`
**依赖：** `implement-repositories`

**实施步骤：**
- [ ] **PermissionController**
  - [ ] 创建 `PermissionController.java`
  - [ ] 创建DTO模型：
    - [ ] `PermissionRequest.java`
    - [ ] `PermissionResponse.java`
  - [ ] `POST /realms/{realmId}/permissions` - 创建权限
  - [ ] `GET /realms/{realmId}/permissions/{id}` - 查询权限
  - [ ] `GET /realms/{realmId}/permissions` - 权限列表（分页、过滤）
  - [ ] `PUT /realms/{realmId}/permissions/{id}` - 更新权限
  - [ ] `DELETE /realms/{realmId}/permissions/{id}` - 删除权限
  - [ ] 添加权限验证
  - [ ] 编写测试

#### 4.5 客户端管理API

**变更ID：** `add-client-management-api`
**OpenSpec：** 需要 ✓
**Capability：** `client-api`
**依赖：** `implement-repositories`

**实施步骤：**
- [ ] **ClientController**
  - [ ] 创建 `ClientController.java`
  - [ ] 创建DTO模型：
    - [ ] `ClientRequest.java`
    - [ ] `ClientResponse.java`
  - [ ] `POST /realms/{realmId}/clients` - 创建客户端
    - [ ] 自动生成client_secret（confidential客户端）
  - [ ] `GET /realms/{realmId}/clients/{id}` - 查询客户端
  - [ ] `GET /realms/{realmId}/clients` - 客户端列表（分页）
  - [ ] `PUT /realms/{realmId}/clients/{id}` - 更新客户端
  - [ ] `DELETE /realms/{realmId}/clients/{id}` - 删除客户端
  - [ ] `POST /realms/{realmId}/clients/{id}/rotate-secret` - 轮换client_secret
  - [ ] 添加权限验证
  - [ ] 编写测试

#### 4.6 审计日志API

**变更ID：** `add-audit-log-api`
**OpenSpec：** 需要 ✓
**Capability：** `audit-api`
**依赖：** `implement-audit-service`

**实施步骤：**
- [ ] **AuditLogController**
  - [ ] 创建 `AuditLogController.java`
  - [ ] 创建DTO模型：
    - [ ] `AuditLogResponse.java`
  - [ ] `GET /realms/{realmId}/audit-logs` - 审计日志查询
    - [ ] 支持分页、时间范围、事件类型过滤
  - [ ] `GET /realms/{realmId}/audit-logs/{id}` - 查询详情
  - [ ] 添加权限验证（审计日志是敏感信息）
  - [ ] 编写测试

#### 4.1 Realm管理
- [ ] **RealmController**
  - `POST /realms` - 创建realm
  - `GET /realms/{id}` - 查询realm
  - `GET /realms` - 列表查询
  - `PUT /realms/{id}` - 更新realm
  - `DELETE /realms/{id}` - 删除realm
  - `POST /realms/{id}/enable` - 启用
  - `POST /realms/{id}/disable` - 禁用

#### 4.2 用户管理
- [ ] **UserController**
  - `POST /realms/{realmId}/users` - 创建用户
  - `GET /realms/{realmId}/users/{id}` - 查询用户
  - `GET /realms/{realmId}/users` - 用户列表（分页）
  - `PUT /realms/{realmId}/users/{id}` - 更新用户
  - `DELETE /realms/{realmId}/users/{id}` - 删除用户
  - `POST /realms/{realmId}/users/{id}/password` - 修改密码
  - `POST /realms/{realmId}/users/{id}/roles` - 分配角色
  - `GET /realms/{realmId}/users/{id}/roles` - 查询用户角色
  - `GET /realms/{realmId}/users/{id}/permissions` - 查询用户权限

#### 4.3 角色管理
- [ ] **RoleController**
  - `POST /realms/{realmId}/roles` - 创建角色
  - `GET /realms/{realmId}/roles/{id}` - 查询角色
  - `GET /realms/{realmId}/roles` - 角色列表
  - `PUT /realms/{realmId}/roles/{id}` - 更新角色
  - `DELETE /realms/{realmId}/roles/{id}` - 删除角色
  - `POST /realms/{realmId}/roles/{id}/permissions` - 分配权限
  - `GET /realms/{realmId}/roles/{id}/permissions` - 查询角色权限

#### 4.4 权限管理
- [ ] **PermissionController**
  - `POST /realms/{realmId}/permissions` - 创建权限
  - `GET /realms/{realmId}/permissions/{id}` - 查询权限
  - `GET /realms/{realmId}/permissions` - 权限列表
  - `PUT /realms/{realmId}/permissions/{id}` - 更新权限
  - `DELETE /realms/{realmId}/permissions/{id}` - 删除权限

#### 4.5 客户端管理
- [ ] **ClientController**
  - `POST /realms/{realmId}/clients` - 创建客户端
  - `GET /realms/{realmId}/clients/{id}` - 查询客户端
  - `GET /realms/{realmId}/clients` - 客户端列表
  - `PUT /realms/{realmId}/clients/{id}` - 更新客户端
  - `DELETE /realms/{realmId}/clients/{id}` - 删除客户端

#### 4.6 审计日志
- [ ] **AuditLogController**
  - `GET /realms/{realmId}/audit-logs` - 审计日志查询
  - `GET /realms/{realmId}/audit-logs/{id}` - 查询详情

### 第五阶段：Spring Security配置

**OpenSpec策略：** 需要OpenSpec提案

---

#### 5.1 Security配置

**变更ID：** `configure-spring-security`
**OpenSpec：** 需要 ✓
**Capability：** `security`
**依赖：** `add-oauth2-token-endpoint`

**实施步骤：**
- [ ] **SecurityConfig**
  - [ ] 创建 `SecurityConfig.java`
  - [ ] 配置密码编码器（BCryptPasswordEncoder bean）
  - [ ] 配置JWT认证过滤器（JwtAuthenticationFilter）
    - [ ] 从请求头提取Bearer token
    - [ ] 验证token
    - [ ] 设置SecurityContext
  - [ ] 配置OAuth2资源服务器
  - [ ] 配置异常处理
    - [ ] `AuthenticationEntryPoint` - 处理401
    - [ ] `AccessDeniedHandler` - 处理403
  - [ ] 配置公开端点（/oauth2/**, /.well-known/**, /actuator/health）
  - [ ] 配置管理API权限（需要admin角色）
  - [ ] 配置CSRF（API禁用，管理界面启用）
  - [ ] 编写集成测试

#### 5.2 CORS和CSRF配置

**变更ID：** `configure-cors-csrf`
**OpenSpec：** 需要 ✓
**Capability：** `security`
**依赖：** `configure-spring-security`

**实施步骤：**
- [ ] **CORS配置**
  - [ ] 创建 `CorsConfig.java`
  - [ ] 配置跨域资源共享
  - [ ] 允许指定origin（配置化）
  - [ ] 允许的methods（GET, POST, PUT, DELETE）
  - [ ] 允许的headers（Authorization, Content-Type）
- [ ] **CSRF配置**
  - [ ] API禁用CSRF（RESTful + JWT）
  - [ ] 管理界面启用CSRF保护（如果有的话）
  - [ ] 编写测试

- [ ] **SecurityConfig**
  - 配置密码编码器（BCrypt）
  - 配置JWT认证过滤器
  - 配置OAuth2资源服务器
  - 配置异常处理（AuthenticationEntryPoint, AccessDeniedHandler）
  - 配置公开端点（/oauth2/**, /.well-known/**）
  - 配置管理API权限（需要管理员角色）

- [ ] **CORS配置**
  - 配置跨域资源共享
  - 允许指定origin、methods、headers

- [ ] **CSRF配置**
  - API禁用CSRF（RESTful + JWT）
  - 管理界面启用CSRF保护

### 第六阶段：错误处理和响应格式

**OpenSpec策略：** 需要OpenSpec提案

---

#### 6.1 统一响应和错误处理

**变更ID：** `add-error-handling`
**OpenSpec：** 需要 ✓
**Capability：** `error-handling`

**实施步骤：**
- [ ] **统一响应格式**
  - [ ] 创建 `ApiResponse.java` 通用响应封装
    ```java
    public class ApiResponse<T> {
        private int code;
        private String message;
        private T data;
        private long timestamp;
    }
    ```
  - [ ] 创建 `ErrorResponse.java` 错误响应封装
    ```java
    public class ErrorResponse {
        private String error;           // OAuth2 error code
        private String error_description;
        private int status;
        private long timestamp;
    }
    ```

- [ ] **全局异常处理**
  - [ ] 创建 `GlobalExceptionHandler.java`
  - [ ] 自定义异常类
    - [ ] `AuthenticationException.java`
    - [ ] `AuthorizationException.java`
    - [ ] `ResourceNotFoundException.java`
    - [ ] `ValidationException.java`
  - [ ] OAuth2标准错误码
    - [ ] invalid_request (400)
    - [ ] invalid_client (401)
    - [ ] invalid_grant (400)
    - [ ] unauthorized_client (401)
    - [ ] unsupported_grant_type (400)
    - [ ] invalid_scope (400)
  - [ ] 配置异常处理器
  - [ ] 编写单元测试

### 第七阶段：测试

**OpenSpec策略：** 直接实现，不需要提案

---

#### 7.1 单元测试

- [ ] **Repository层测试**
  - [ ] 配置H2内存数据库（测试环境）
  - [ ] 创建测试schema初始化脚本
  - [ ] 编写Repository测试
    - [ ] RealmRepositoryTest
    - [ ] UserRepositoryTest
    - [ ] RoleRepositoryTest
    - [ ] PermissionRepositoryTest
    - [ ] ClientRepositoryTest
    - [ ] TokenRepositoryTest
    - [ ] AuditLogRepositoryTest

- [ ] **Service层测试**
  - [ ] PasswordServiceTest
  - [ ] JwtServiceTest
  - [ ] AuthenticationServiceTest
  - [ ] AuthorizationServiceTest
  - [ ] AuditServiceTest

- [ ] **其他测试**
  - [ ] JWT生成和验证测试
  - [ ] 密码加密测试
  - [ ] 异常处理测试

#### 7.2 集成测试

- [ ] **OAuth2流程测试**
  - [ ] 授权码流程测试
  - [ ] Token刷新测试
  - [ ] 客户端凭证模式测试
  - [ ] 密码模式测试（可选）

- [ ] **权限检查测试**
  - [ ] 权限检查测试
  - [ ] 策略评估测试

- [ ] **API端点测试**
  - [ ] Realm管理API测试
  - [ ] 用户管理API测试
  - [ ] 角色管理API测试
  - [ ] 客户端管理API测试

**测试覆盖率目标：**
- Repository层：> 90%
- Service层：> 80%
- Controller层：> 70%

**运行测试：**
```bash
# 单元测试
mvn test

# 集成测试
mvn verify

# 测试覆盖率
mvn test jacoco:report
```

- [ ] **单元测试**
  - Repository层测试（使用H2内存数据库）
  - Service层测试（Mock依赖）
  - JWT生成和验证测试
  - 密码加密测试

- [ ] **集成测试**
  - OAuth2授权流程测试
  - Token刷新测试
  - 权限检查测试
  - API端点测试

### 第八阶段：运维支持

**OpenSpec策略：** 直接实现，不需要提案

---

#### 8.1 健康检查

- [ ] **Spring Boot Actuator集成**
  - [ ] 添加actuator依赖
  - [ ] 配置actuator端点
    - [ ] `/actuator/health` - 健康检查
      - [ ] 检查数据库连接
      - [ ] 检查JVM状态
    - [ ] `/actuator/metrics` - 应用指标
    - [ ] `/actuator/info` - 应用信息
  - [ ] 配置health check端点（自定义）
    - [ ] 检查JWT密钥加载
    - [ ] 检查数据库schema

#### 8.2 配置管理

- [ ] **支持环境变量覆盖**
  - [ ] 数据库配置（URL, username, password）
  - [ ] JWT配置（过期时间、密钥路径）
  - [ ] 应用配置（port, log level）

- [ ] **支持配置中心（可选）**
  - [ ] Spring Cloud Config集成（可选）
  - [ ] Nacos集成（可选）

#### 8.3 日志和监控

- [ ] **配置应用日志**
  - [ ] 配置logback-spring.xml
  - [ ] 分环境日志配置（dev, test, prod）
  - [ ] 日志级别动态调整

- [ ] **配置JMX监控**
  - [ ] 暴露JMX端点
  - [ ] 配置JMX监控指标

- [ ] **配置分布式追踪（可选）**
  - [ ] Spring Cloud Sleuth集成（可选）
  - [ ] Zipkin集成（可选）

#### 8.4 Docker部署

- [ ] **Docker镜像构建**
  - [ ] 创建Dockerfile
  - [ ] 多阶段构建（build阶段 + runtime阶段）
  - [ ] 优化镜像大小

- [ ] **Docker Compose配置**
  - [ ] docker-compose.yml
    - [ ] PostgreSQL容器
    - [ ] 应用容器
    - [ ] 网络配置
    - [ ] 环境变量配置

- [ ] **生产环境配置指南**
  - [ ] 环境变量文档
  - [ ] 数据库初始化脚本
  - [ ] 部署检查清单

- [ ] **健康检查**
  - Spring Boot Actuator集成
  - `/actuator/health` - 健康检查
  - `/actuator/metrics` - 应用指标

- [ ] **配置管理**
  - 支持环境变量覆盖
  - 支持配置中心（可选）

- [ ] **日志和监控**
  - 配置应用日志
  - 配置JMX监控
  - 配置分布式追踪（可选）

### 第九阶段：文档

**OpenSpec策略：** 直接实现，不需要提案

---

#### 9.1 API文档

- [ ] **Swagger/OpenAPI集成**
  - [ ] 添加springdoc-openapi依赖
  - [ ] 配置OpenAPI信息（title, version, description）
  - [ ] 配置JWT认证（SecurityScheme）
  - [ ] 配置API分组（OAuth2端点、管理API）

- [ ] **API使用示例**
  - [ ] 创建Postman Collection
  - [ ] 创建curl示例脚本
  - [ ] 创建API使用指南

#### 9.2 部署文档

- [ ] **Docker镜像构建**
  - [ ] Dockerfile说明
  - [ ] 构建命令示例

- [ ] **Docker Compose配置**
  - [ ] docker-compose.yml说明
  - [ ] 启动/停止命令

- [ ] **生产环境配置指南**
  - [ ] 环境变量列表
  - [ ] 数据库初始化步骤
  - [ ] 安全配置建议
  - [ ] 性能调优建议

#### 9.3 开发文档

- [ ] **架构文档**
  - [ ] 系统架构图
  - [ ] 数据流图
  - [ ] 模块交互图

- [ ] **开发者指南**
  - [ ] 开发环境搭建
  - [ ] 代码规范
  - [ ] 测试规范
  - [ ] Git工作流（如果使用）

- [ ] **API文档**
  - Swagger/OpenAPI集成
  - API使用示例

- [ ] **部署文档**
  - Docker镜像构建
  - Docker Compose配置（PostgreSQL + App）
  - 生产环境配置指南

---

## 技术栈补充

### 需要添加的依赖
```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
</dependency>

<!-- OAuth2 Resource Server -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- H2 Database for testing -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 开发顺序建议

### 最小可行产品（MVP）路径

**目标：** 实现基础的用户认证和token管理功能

| 阶段 | 变更ID | 描述 | 预计时间 | OpenSpec |
|------|--------|------|---------|----------|
| 1 | `run-jooq-codegen` | JOOQ代码生成 | 0.5天 | ✗ 直接实现 |
| 2 | `implement-repositories` | Repository实现 | 2-3天 | ✓ 需要 |
| 3 | `implement-password-service` | 密码服务 | 0.5天 | ✓ 需要 |
| 4 | `implement-jwt-service` | JWT服务 | 1天 | ✓ 需要 |
| 5 | `implement-authentication-service` | 认证服务 | 1-2天 | ✓ 需要 |
| 6 | `add-oauth2-token-endpoint` | OAuth2 Token端点 | 1-2天 | ✓ 需要 |
| 7 | `configure-spring-security` | Security配置 | 1天 | ✓ 需要 |
| 8 | `add-user-management-api` | 用户管理API | 1-2天 | ✓ 需要 |
| 9 | 单元测试 + 集成测试 | 测试 | 2-3天 | ✗ 直接实现 |

**MVP功能范围：**
- ✅ 用户注册、登录、登出
- ✅ Token签发和刷新
- ✅ 基础的用户管理
- ✅ 基本的认证和授权

---

### 完整功能路径

在MVP基础上，依次完成以下变更：

| 阶段 | 变更ID | 描述 | 依赖 | 优先级 |
|------|--------|------|------|--------|
| 10 | `add-userinfo-endpoint` | UserInfo端点 | add-oauth2-token-endpoint | HIGH |
| 11 | `add-jwks-endpoint` | JWKS端点 | implement-jwt-service | HIGH |
| 12 | `add-oauth2-authorization-endpoint` | OAuth2授权码流程 | add-oauth2-token-endpoint | HIGH |
| 13 | `add-oidc-discovery-endpoint` | OIDC发现端点 | add-oauth2-token-endpoint, add-userinfo-endpoint | MEDIUM |
| 14 | `implement-authorization-service` | 授权服务 | implement-repositories | HIGH |
| 15 | `implement-audit-service` | 审计服务 | implement-repositories | MEDIUM |
| 16 | `configure-cors-csrf` | CORS/CSRF配置 | configure-spring-security | MEDIUM |
| 17 | `add-error-handling` | 统一错误处理 | 无 | MEDIUM |
| 18 | `add-realm-management-api` | Realm管理API | implement-repositories | MEDIUM |
| 19 | `add-role-management-api` | 角色管理API | implement-repositories | MEDIUM |
| 20 | `add-permission-management-api` | 权限管理API | implement-repositories | MEDIUM |
| 21 | `add-client-management-api` | 客户端管理API | implement-repositories | MEDIUM |
| 22 | `add-audit-log-api` | 审计日志API | implement-audit-service | LOW |
| 23 | 运维支持 | Actuator、日志、Docker | - | LOW |
| 24 | 文档 | API文档、部署文档 | - | LOW |

**完整功能范围（MVP + 额外功能）：**
- ✅ MVP所有功能
- ✅ OAuth2授权码流程
- ✅ OIDC完整支持
- ✅ 高级授权（策略评估）
- ✅ 审计日志
- ✅ 完整的测试覆盖
- ✅ 运维监控
- ✅ API文档

---

## OpenSpec实施路线图

### 阶段1：基础设施（Week 1-2）

**目标：** 完成数据访问层

```
Week 1
├─ Day 1-2: run-jooq-codegen（直接实现）
├─ Day 3-5: implement-repositories（创建提案 + 实施）
└─ Week 1归档

Week 2
├─ Day 1: implement-password-service（创建提案 + 实施）
├─ Day 2-3: implement-jwt-service（创建提案 + 实施）
├─ Day 4-5: implement-authentication-service（创建提案 + 实施）
└─ Week 2归档
```

**验收标准：**
- ✅ 所有Repository通过单元测试
- ✅ 可以生成和验证JWT token
- ✅ 可以进行用户登录并返回token

---

### 阶段2：OAuth2协议（Week 3-4）

**目标：** 完成OAuth2/OIDC核心功能

```
Week 3
├─ Day 1-3: add-oauth2-token-endpoint（创建提案 + 实施）
├─ Day 4: add-userinfo-endpoint（创建提案 + 实施）
├─ Day 5: add-jwks-endpoint（创建提案 + 实施）
└─ Week 3归档

Week 4
├─ Day 1-2: add-oauth2-authorization-endpoint（创建提案 + 实施）
├─ Day 3: add-oidc-discovery-endpoint（创建提案 + 实施）
├─ Day 4: add-user-management-api（创建提案 + 实施）
├─ Day 5: configure-spring-security（创建提案 + 实施）
└─ Week 4归档
```

**验收标准：**
- ✅ 可以通过OAuth2授权码流程获取token
- ✅ Token端点支持所有grant types
- ✅ UserInfo端点返回用户信息
- ✅ JWKS端点可以验证JWT签名

---

### 阶段3：高级功能（Week 5-6）

**目标：** 完成授权、审计和管理API

```
Week 5
├─ Day 1-2: implement-authorization-service（创建提案 + 实施）
├─ Day 3: implement-audit-service（创建提案 + 实施）
├─ Day 4-5: configure-cors-csrf + add-error-handling（创建提案 + 实施）
└─ Week 5归档

Week 6
├─ Day 1-3: add-realm-management-api + add-role-management-api
├─ Day 4-5: add-permission-management-api + add-client-management-api
└─ Week 6归档
```

**验收标准：**
- ✅ 可以进行细粒度的权限检查
- ✅ 所有关键操作都有审计日志
- ✅ 可以通过管理API管理所有资源

---

### 阶段4：测试和部署（Week 7-8）

**目标：** 完善测试、文档和部署

```
Week 7
├─ Day 1-3: 单元测试和集成测试
├─ Day 4: add-audit-log-api（创建提案 + 实施）
├─ Day 5: 运维支持（Actuator、日志）
└─ Week 7归档

Week 8
├─ Day 1-2: API文档（Swagger/OpenAPI）
├─ Day 3-4: 部署文档（Docker、配置）
├─ Day 5: 最终集成测试和发布准备
└─ Week 8归档
```

**验收标准：**
- ✅ 测试覆盖率 > 80%
- ✅ API文档完整
- ✅ 可以通过Docker快速部署
- ✅ 生产环境配置指南完整

---

## 注意事项

### 1. 安全性
- 所有敏感信息必须加密存储（密码、client_secret）
- JWT必须使用强签名算法（RS256）
- 实现token黑名单机制（可选）
- 使用HTTPS传输（生产环境）
- 实施Rate Limiting防止暴力破解

### 2. 性能
- Repository查询使用索引优化
- Token验证结果缓存（Redis或本地缓存）
- 数据库连接池配置（HikariCP）
- 异步处理审计日志
- 分页查询（避免全表扫描）

### 3. 可扩展性
- Service接口设计便于扩展
- 策略引擎支持自定义表达式语言
- 支持多种数据库（PostgreSQL优先，MySQL可选）
- 支持水平扩展（无状态设计）

### 4. 可测试性
- Repository层使用H2测试
- Service层支持Mock
- 集成测试覆盖核心流程
- API测试使用契约测试（可选）

### 5. OpenSpec最佳实践
- **大功能必提案，小改动直接做**
  - Repository/Service/Controller实现 → 提案
  - Bug修复/格式调整 → 直接做

- **先提案，后实施**
  - 创建proposal.md → 验证 → 批准 → 实施 → 归档

- **保持文档同步**
  - 提案批准后按tasks实施
  - 完成后更新tasks.md
  - 部署后归档变更

- **使用工具辅助**
  - 使用 `todowrite` 跟踪进度
  - 使用 `openspec validate` 验证提案
  - 使用 `openspec archive` 归档完成的工作

### 6. 代码规范
- 遵循 `openspec/project.md` 中的代码风格
- 使用JOOQ的类型安全查询
- 使用Optional避免NPE
- 统一的异常处理
- 统一的响应格式（ApiResponse<T>）

### 7. 版本管理
- 使用语义化版本（Semantic Versioning）
- 主版本号：不兼容的API变更
- 次版本号：向下兼容的功能新增
- 修订号：向下兼容的问题修正

### 8. 文档维护
- API变更后及时更新Swagger文档
- 重要决策记录在`OPENSPEC_WORKFLOW_GUIDE.md`
- 每次归档后更新`DEVELOPMENT_PLAN.md`状态

---

## 目录结构建议

```
src/main/java/com/owiseman/
├── App.java                          # Spring Boot启动类
├── config/                           # 配置类
│   ├── SecurityConfig.java
│   ├── JooqConfig.java
│   └── JwtConfig.java
├── controller/                        # Controller层
│   ├── oauth2/
│   │   ├── AuthorizationController.java
│   │   ├── TokenController.java
│   │   ├── UserInfoController.java
│   │   └── JwksController.java
│   ├── api/
│   │   ├── RealmController.java
│   │   ├── UserController.java
│   │   ├── RoleController.java
│   │   ├── PermissionController.java
│   │   ├── ClientController.java
│   │   └── AuditLogController.java
│   └── model/                         # DTO/Request/Response
│       ├── ApiResponse.java
│       ├── ErrorResponse.java
│       ├── LoginRequest.java
│       └── TokenResponse.java
├── service/                           # Service层
│   ├── AuthenticationService.java
│   ├── AuthorizationService.java
│   ├── PasswordService.java
│   ├── JwtService.java
│   └── AuditService.java
├── core/
│   ├── domain/                        # Domain模型
│   └── jooq/
│       ├── repository/               # Repository接口
│       ├── repository/impl/          # Repository实现
│       └── records/                   # JOOQ生成的记录类
└── exception/                        # 异常类
    ├── AuthenticationException.java
    ├── AuthorizationException.java
    └── GlobalExceptionHandler.java

src/test/java/com/owiseman/
├── repository/                        # Repository测试
├── service/                           # Service测试
└── integration/                       # 集成测试
```

---

## 下一步行动

建议按以下顺序开始实施：

### 第一步：开始第一个变更

**变更ID：** `run-jooq-codegen`

这是一个配置任务，不需要OpenSpec提案。执行以下命令：

```bash
# 1. 设置数据库密码环境变量
export PGPASSWORD=your_postgres_password

# 2. 运行JOOQ代码生成
mvn jooq-codegen:generate

# 3. 验证生成的类
ls src/main/java/com/owiseman/core/jooq/records/
```

---

### 第二步：创建第一个OpenSpec提案

**变更ID：** `implement-repositories`

按照OpenSpec workflow创建提案：

```bash
# 1. 检查是否有未完成的变更
openspec list

# 2. 创建提案目录
mkdir -p openspec/changes/implement-repositories/specs/repository

# 3. 编写提案文件
# - openspec/changes/implement-repositories/proposal.md
# - openspec/changes/implement-repositories/tasks.md
# - openspec/changes/implement-repositories/specs/repository/spec.md

# 4. 验证提案
openspec validate implement-repositories --strict

# 5. 等待批准后开始实施
```

**参考模板：** 查看 `OPENSPEC_WORKFLOW_GUIDE.md` 中的示例1

---

### 实施检查清单

在开始任何实施前，请确认：

```bash
# 1. 了解项目规范
cat openspec/project.md

# 2. 查看活跃的变更
openspec list

# 3. 查看已实现的规范
openspec spec list --long

# 4. 查看具体变更详情
openspec show <change-id>

# 5. 查看任务清单
cat openspec/changes/<change-id>/tasks.md
```

---

### 快速开始命令参考

```bash
# 查看所有命令
openspec --help

# 查看活跃变更
openspec list

# 查看所有规范
openspec list --specs

# 查看规范详情
openspec spec list --long

# 查看变更详情
openspec show <change-id>

# 验证变更
openspec validate <change-id> --strict

# 归档变更（部署后）
openspec archive <change-id> --yes
```

---

## 文档索引

| 文档 | 描述 | 用途 |
|------|------|------|
| `openspec/project.md` | 项目规范 | 了解项目技术栈、代码风格、架构规范 |
| `openspec/AGENTS.md` | OpenSpec官方文档 | 完整的OpenSpec workflow说明 |
| `OPENSPEC_WORKFLOW_GUIDE.md` | OpenSpec工作流指南 | 在MY-IMA项目中如何使用OpenSpec |
| `AI_QUICK_REFERENCE.md` | AI助手快速参考 | 快速决策树、命令参考、常见错误 |
| `DEVELOPMENT_PLAN.md` | 开发计划 | 完整的开发计划和路线图（本文档） |

---

## 支持和帮助

### 遇到问题？

1. **不知道是否需要提案** → 查看 `AI_QUICK_REFERENCE.md` 的"快速决策树"
2. **提案格式错误** → 运行 `openspec validate <change-id> --strict`
3. **找不到相关规范** → 运行 `openspec spec list --long`
4. **不知道如何实施** → 读取 `openspec/changes/<change-id>/tasks.md`
5. **需要更详细的指导** → 阅读 `OPENSPEC_WORKFLOW_GUIDE.md`

### 常见问题

**Q: 如何判断一个任务是bug还是新功能？**

A:
- **Bug的判断标准：**
  - 代码没有按照spec中定义的行为工作
  - 测试用例失败，但spec没有改变

- **新功能的判断标准：**
  - 引入了新的能力或端点
  - 改变了已有的接口契约
  - 需要修改database schema
  - 影响多个模块或服务

**Q: 提案中的tasks.md可以修改吗？**

A: 可以，但需要注意：
- 如果只是增加更细粒度的步骤，直接添加即可
- 如果改变了任务范围，需要更新proposal.md的Impact部分
- 如果删除了某些任务，确保它们确实不需要

**Q: 多个AI助手如何协作？**

A:
1. 在开始任何工作前，运行 `openspec list` 查看是否有未完成的变更
2. 如果有未完成的变更，检查是否与你的工作冲突
3. 如果冲突，协调分工或合并提案
4. 如果不冲突，可以在不同的change下并行工作

---

## 总结

通过这个开发计划，我们将：

1. **系统化开发** - 每个功能都经过提案、实施、归档的完整流程
2. **规范驱动** - 所有变更都有明确的规范定义和验收标准
3. **可追踪** - 每个任务都有清晰的状态和依赖关系
4. **可扩展** - 清晰的架构和模块化设计便于后续扩展
5. **高质量** - 完整的测试覆盖和文档支持

---

## 开始开发

准备好开始了吗？执行以下第一步：

```bash
# 1. 运行JOOQ代码生成
export PGPASSWORD=your_password
mvn jooq-codegen:generate

# 2. 创建第一个OpenSpec提案
mkdir -p openspec/changes/implement-repositories/specs/repository

# 3. 按照模板编写提案文件
# 参考: OPENSPEC_WORKFLOW_GUIDE.md 的示例1

# 4. 验证提案
openspec validate implement-repositories --strict
```

**需要我帮你创建第一个OpenSpec提案吗？**

我可以帮你创建 `implement-repositories` 提案的完整文件，包括 proposal.md, tasks.md 和 spec.md。
