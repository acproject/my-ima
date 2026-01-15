# Project Context

## Purpose
A lightweight, embeddable, and extensible Identity and Access Management (IAM) / Auth Server built with Spring Boot and JOOQ. The system provides multi-tenant authentication, authorization, and user management capabilities for integrating identity management into applications.

## Tech Stack
- **Java 21** - Primary language with modern Java features
- **Spring Boot 4.0.1** - Application framework and dependency injection
- **Spring Security** - Security and authentication framework
- **JOOQ 3.19.15** - Type-safe SQL query builder and ORM
- **PostgreSQL** - Primary relational database
- **Lombok** - Reduces boilerplate code with annotations
- **Maven** - Build and dependency management tool

## Project Conventions

### Code Style
- Standard Java naming conventions (PascalCase for classes, camelCase for methods/variables, UPPER_SNAKE_CASE for constants)
- POJO domain models with explicit getters and setters (no Lombok @Data in core domain)
- Repository interfaces in `com.owiseman.core.jooq.repository` with implementations in `impl` subpackage
- Domain models in `com.owiseman.core.domain` using primitive wrapper types (Boolean, Integer) for nullable fields
- Lombok used sparingly with `@Optional` marker for tool-generated code
- Package structure follows feature/layer separation: `com.owiseman.core.[layer]`

### Architecture Patterns
- **Repository Pattern** - Abstractions for data access with JOOQ implementations
- **Domain-Driven Design** - Core domain models (User, Realm, Role, Permission, Policy, Client, Token, AuditLog)
- **Multi-tenant Architecture** - Realm-based isolation for tenant separation
- **Database-first with JOOQ Codegen** - Schema drives type-safe Java code generation
- **Type-Safe Queries** - JOOQ generates compile-time checked SQL queries from database schema
- **Layered Architecture**:
  - Domain: Core business entities
  - Repository: Data access abstractions and implementations
  - Application: Spring Boot configuration and entry points

### Testing Strategy
- Spring Boot Test for integration testing
- JOOQ Test support for database testing
- Spring Security Test for security-related tests
- Tests should verify repository implementations and domain logic
- Focus on testing core IAM functionality: user management, authentication, authorization

### Git Workflow
- Not yet established (repository not initialized as git repo)
- Consider using conventional commits when established

## Domain Context

### Core Entities
- **Realm** - Tenant isolation unit, each realm maintains independent users, roles, policies
- **User** - End user accounts with credentials, attributes, and realm association
- **Role** - Collections of permissions assigned to users
- **Permission** - Granular access controls linked to policies
- **Policy** - Authorization rules (PermissionPolicy mapping)
- **Client** - OAuth/OpenID client applications (ClientType enum: confidential, public)
- **Token** - Authentication tokens with types (TokenType: access, refresh, id_token)
- **AuditLog** - Security event tracking with event types and subject types

### Key Relationships
- Users belong to exactly one Realm (realmId)
- Users can have multiple Roles (UserRole)
- Roles have multiple Permissions (RolePermission)
- Permissions are governed by Policies (PermissionPolicy)
- Clients can request tokens from Token endpoints
- All actions generate AuditLog entries

### Multi-tenancy Model
- Realm-based isolation enables SaaS or multi-organization scenarios
- All repository queries must scope by realmId where applicable
- Realms have independent configuration (config map)
- Users, roles, permissions are realm-scoped

## Important Constraints
- **Embeddable Design** - System must be embeddable into other applications, not just standalone deployment
- **Lightweight Footprint** - Minimize dependencies and runtime overhead
- **Database Required** - PostgreSQL connection required (localhost:5432 by default)
- **Code Generation Workflow** - JOOQ codegen must run after schema changes (Maven plugin configured)
- **Java 21 Required** - Modern Java features used throughout codebase
- **Security-First** - Passwords must be hashed, tokens properly managed, audit trail maintained

## External Dependencies
- **PostgreSQL Database** - Primary data store (jdbc:postgresql://localhost:5432/my-iam)
- **Environment Variables** - Database credentials via PGPASSWORD environment variable for JOOQ codegen
- **No External APIs** - Currently self-contained, no external service dependencies
