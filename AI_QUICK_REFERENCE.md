# MY-IMA 项目 AI助手快速指南

## 🚀 快速决策树

当你收到请求时，按以下流程判断：

```
请求是什么？
├─ Bug修复（恢复预期行为）→ 直接实现，无需提案 ✗
├─ 代码格式/注释/文档 → 直接修改 ✗
├─ 依赖升级（非破坏性）→ 直接修改 ✗
├─ 添加单元测试 → 直接编写 ✗
├─ 性能优化（不改变行为）→ 直接优化 ✗
├─ 新功能/新能力 → 创建OpenSpec提案 ✓
├─ API端点变更 → 创建OpenSpec提案 ✓
├─ 数据库Schema变更 → 创建OpenSpec提案 ✓
├─ 架构变更 → 创建OpenSpec提案 ✓
└─ 不确定 → 创建OpenSpec提案（更安全）✓
```

---

## 📋 开始工作前的检查清单

**必做项：**
```bash
# 1. 了解项目规范
cat openspec/project.md

# 2. 查看活跃的变更（是否有人正在做相关工作）
openspec list

# 3. 查看已实现的规范（了解现有能力）
openspec spec list --long
```

**如果需要创建提案：**
```bash
# 4. 查看完整的OpenSpec流程
cat openspec/AGENTS.md
cat OPENSPEC_WORKFLOW_GUIDE.md
```

**如果已经有相关提案：**
```bash
# 5. 查看提案详情
openspec show <change-id>

# 6. 查看任务清单
cat openspec/changes/<change-id>/tasks.md
```

---

## 🎯 针对DEVELOPMENT_PLAN.md的具体策略

### 需要OpenSpec提案的变更（按阶段）

**阶段1：基础设施层**
- ✓ `implement-repositories` - 实现所有Repository（6个）
- ✓ `run-jooq-codegen` - JOOQ代码生成（可以作为前置任务）

**阶段2：核心服务层**
- ✓ `implement-password-service` - 密码加密服务
- ✓ `implement-jwt-service` - JWT生成和验证
- ✓ `implement-authentication-service` - 认证服务
- ✓ `implement-authorization-service` - 授权服务
- ✓ `implement-audit-service` - 审计日志服务

**阶段3：OAuth2/OIDC协议**
- ✓ `add-oauth2-token-endpoint` - Token端点
- ✓ `add-oauth2-authorization-endpoint` - 授权码流程
- ✓ `add-userinfo-endpoint` - UserInfo端点
- ✓ `add-jwks-endpoint` - JWKS端点
- ✓ `add-oidc-discovery-endpoint` - OIDC发现端点

**阶段4：管理API**
- ✓ `add-realm-management-api` - Realm管理API
- ✓ `add-user-management-api` - 用户管理API
- ✓ `add-role-management-api` - 角色管理API
- ✓ `add-permission-management-api` - 权限管理API
- ✓ `add-client-management-api` - 客户端管理API
- ✓ `add-audit-log-api` - 审计日志API

**阶段5-6：Security和配置**
- ✓ `configure-spring-security` - Spring Security配置
- ✓ `configure-cors-csrf` - CORS和CSRF配置
- ✓ `add-error-handling` - 统一错误处理

### 不需要提案的工作

**阶段7：测试**
- ✗ 单元测试（直接编写）
- ✗ 集成测试（直接编写）

**阶段8：运维**
- ✗ 健康检查端点（直接实现）
- ✗ 监控配置（直接配置）

**阶段9：文档**
- ✗ API文档（直接编写）
- ✗ 部署文档（直接编写）

---

## 🛠️ 实施工作流

### 情况1：直接实现（无需提案）

**适用场景：** Bug修复、格式调整、添加测试

**步骤：**
1. 直接修改代码
2. 编写测试
3. 验证功能

**示例：**
```bash
# 修复JooqUserRepository编译错误
# 直接修改文件，无需OpenSpec
```

### 情况2：实施已有的提案

**适用场景：** `openspec list` 显示已有相关提案

**步骤：**
```bash
# 1. 查看提案
openspec show <change-id>
cat openspec/changes/<change-id>/proposal.md
cat openspec/changes/<change-id>/tasks.md

# 2. 使用TODO跟踪进度（AI工具）
todowrite --todos '[...]'

# 3. 按顺序完成tasks.md中的任务
# - 完成一项，勾选一项
# - 遵循 openspec/project.md 的代码风格

# 4. 完成后更新tasks.md
# - 确保所有项都标记为 [x]
```

### 情况3：创建新提案并实施

**适用场景：** 新功能、架构变更

**步骤：**
```bash
# 1. 检查是否有相关提案
openspec list
openspec spec list --long

# 2. 选择唯一的change-id
# 格式: kebab-case, verb-led
# 例如: implement-repositories, add-oauth2-token-endpoint

# 3. 创建目录
mkdir -p openspec/changes/<change-id>/specs/<capability>

# 4. 编写文件
# - proposal.md (Why, What, Impact)
# - tasks.md (Implementation checklist)
# - specs/<capability>/spec.md (ADDED Requirements with Scenarios)

# 5. 验证提案
openspec validate <change-id> --strict

# 6. 等待批准
# 不要开始实现，直到批准

# 7. 批准后，按情况2的步骤实施
```

---

## 📝 创建提案的快速模板

### proposal.md 模板

```markdown
# Change: [简短描述]

## Why
[1-2句话说明问题或机会]

## What Changes
- [变更项1]
- [变更项2]
- [如果有破坏性变更，标记为 **BREAKING**]

## Impact
- Affected specs: [影响的capability列表]
- Affected code: [受影响的包或类]
- Dependencies: [依赖的前置工作]
```

### tasks.md 模板

```markdown
## 1. Prerequisites
- [ ] 1.1 [前置任务1]
- [ ] 1.2 [前置任务2]

## 2. Implementation
- [ ] 2.1 [实现步骤1]
- [ ] 2.2 [实现步骤2]

## 3. Testing
- [ ] 3.1 [测试步骤1]
- [ ] 3.2 [测试步骤2]
```

### spec.md 模板

```markdown
## ADDED Requirements

### Requirement: [需求名称]
[需求描述，使用SHALL/MUST]

#### Scenario: [场景名称]
- **GIVEN** [前置条件]
- **WHEN** [操作]
- **THEN** [预期结果]
```

**关键规则：**
- 每个 `## ADDED Requirements` 下至少一个 `#### Scenario:`
- Scenario必须是 `#### Scenario: 名称`（4个#）
- 必须使用 `- **GIVEN**`, `- **WHEN**`, `- **THEN**` 格式

---

## ✅ 完成后的检查清单

### 实施完成后

```bash
# 1. 确认tasks.md中所有项都已勾选
cat openspec/changes/<change-id>/tasks.md

# 2. 运行测试
mvn test

# 3. 验证功能
# 手动测试或运行集成测试

# 4. 提交代码（如果使用git）
git add .
git commit -m "feat: implement [change-name]"
```

### 归档（部署到生产后）

```bash
# 1. 确认已部署到生产环境

# 2. 归档变更
openspec archive <change-id> --yes

# 3. 验证归档
openspec validate --strict

# 4. 确认specs已更新
ls openspec/specs/
```

---

## 🔥 快速参考命令

```bash
# 查看状态
openspec list                  # 活跃的变更
openspec list --specs          # 已实现的规范
openspec spec list --long      # 规范详情

# 查看详情
openspec show <item>           # 查看变更或规范
openspec show <change-id> --json --deltas-only  # 查看变更的delta

# 验证
openspec validate <change-id> --strict    # 验证变更
openspec validate --strict               # 验证所有

# 归档
openspec archive <change-id> --yes       # 归档变更
openspec archive <change-id> --skip-specs --yes  # 仅归档不更新specs

# 搜索
rg -n "Requirement:|Scenario:" openspec/specs  # 全文搜索specs
```

---

## ⚠️ 常见错误和避免方法

### 错误1：忘记验证提案
**问题：** 提案格式错误，实施时才发现
**避免：** 每次创建提案后运行 `openspec validate <change-id> --strict`

### 错误2：Scenario格式错误
**问题：** 使用了 `### Scenario:` 而不是 `#### Scenario:`
**避免：** 记住Scenario是4个#（`####`）

### 错误3：MODIFIED时没有复制完整文本
**问题：** 只写了修改部分，导致归档时丢失原有内容
**避免：** MODIFIED时必须复制完整的requirement（包括所有scenarios）

### 错误4：在没有提案的情况下实施大功能
**问题：** 代码完成后没有对应的spec文档
**避免：** 任何新功能或API变更都必须先创建提案

### 错误5：在未批准的情况下开始实施
**问题：** 需求变更导致返工
**避免：** 严格遵循"先批准，后实施"原则

---

## 🎓 学习资源

1. **OpenSpec完整指南**：`OPENSPEC_WORKFLOW_GUIDE.md`
2. **OpenSpec官方文档**：`openspec/AGENTS.md`
3. **项目规范**：`openspec/project.md`
4. **开发计划**：`DEVELOPMENT_PLAN.md`

---

## 📞 遇到问题？

1. **不知道是否需要提案** → 查看本文档的"快速决策树"
2. **提案格式错误** → 运行 `openspec validate <change-id> --strict` 查看错误信息
3. **找不到相关规范** → 运行 `openspec spec list --long` 查看所有规范
4. **不知道如何实施** → 读取 `openspec/changes/<change-id>/tasks.md` 获取步骤
5. **需要更详细的指导** → 阅读 `OPENSPEC_WORKFLOW_GUIDE.md`

---

## 💡 最佳实践总结

1. **先检查，再决策**
   - 运行 `openspec list` 查看是否已有相关变更
   - 阅读相关规范了解现有能力

2. **大功能必提案，小改动直接做**
   - Repository/Service/Controller实现 → 提案
   - Bug修复/格式调整 → 直接做

3. **遵循格式规范**
   - Scenario必须是 `#### Scenario:`
   - 使用 `- **GIVEN**` / `- **WHEN**` / `- **THEN**`

4. **保持文档同步**
   - 提案批准后按tasks实施
   - 完成后更新tasks.md
   - 部署后归档变更

5. **使用工具辅助**
   - 使用 `todowrite` 跟踪进度
   - 使用 `openspec validate` 验证提案
   - 使用 `openspec archive` 归档完成的工作

通过遵循这个指南，AI助手可以在MY-IMA项目中高效、规范地完成所有开发任务。
