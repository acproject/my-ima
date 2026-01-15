# Tasks: Implement Core Repositories

## 1. Prerequisites
- [ ] 1.1 Ensure PostgreSQL is running on localhost:5432
- [ ] 1.2 Ensure database schema is initialized (run src/main/resources/sql/ima_sql.sql)
- [ ] 1.3 Run JOOQ code generation: `mvn jooq-codegen:generate`
- [ ] 1.4 Verify generated classes exist in `src/main/java/com/owiseman/core/jooq/records/`
- [ ] 1.5 Verify no compilation errors after code generation

## 2. RealmRepository Implementation
- [x] 2.1 Create `RealmRepository.java` interface with methods:
  - `findById(UUID id)`
  - `findByName(UUID realmId, String name)`
  - `create(Realm realm)`
  - `update(Realm realm)`
  - `enable(UUID id)`
  - `disable(UUID id)`
- [x] 2.2 Create `JooqRealmRepository.java` implementation
- [x] 2.3 Implement findById using JOOQ DSLContext
- [x] 2.4 Implement findByName with realmId filter
- [x] 2.5 Implement create with auto-generated UUID
- [x] 2.6 Implement update with optimistic locking (if needed)
- [x] 2.7 Implement enable/disable methods
- [x] 2.8 Add exception handling (ResourceNotFoundException)
- [ ] 2.9 Write unit tests for RealmRepository

## 3. UserRepository Implementation
- [x] 3.1 Fix compilation error in `JooqUserRepository.java`
- [x] 3.2 Update `UserRepository.java` interface with complete method signatures:
  - `findById(UUID id)`
  - `findByUsername(UUID realmId, String username)`
  - `findByEmail(UUID realmId, String email)`
  - `findAll(UUID realmId, int offset, int limit)`
  - `create(User user)`
  - `update(User user)`
  - `delete(UUID id)`
  - `findPermissions(UUID userId)`
  - `assignRole(UUID userId, UUID roleId)`
  - `removeRole(UUID userId, UUID roleId)`
  - `findUserRoles(UUID userId)`
- [x] 3.3 Implement findByUsername using JOOQ type-safe query
- [x] 3.4 Implement findByEmail
- [x] 3.5 Implement findAll with pagination
- [x] 3.6 Implement create with BCrypt password hashing
- [x] 3.7 Implement update
- [x] 3.8 Implement delete (soft delete)
- [x] 3.9 Implement findPermissions with role-permission join
- [x] 3.10 Implement assignRole (insert into user_role table)
- [x] 3.11 Implement removeRole (delete from user_role table)
- [x] 3.12 Implement findUserRoles
- [x] 3.13 Add exception handling
- [ ] 3.14 Write comprehensive unit tests

## 4. RoleRepository Implementation
- [ ] 4.1 Create `RoleRepository.java` interface with methods:
  - `findById(UUID id)`
  - `findByName(UUID realmId, String name)`
  - `findAll(UUID realmId, int offset, int limit)`
  - `create(Role role)`
  - `update(Role role)`
  - `delete(UUID id)`
  - `assignPermission(UUID roleId, UUID permissionId)`
  - `removePermission(UUID roleId, UUID permissionId)`
  - `findPermissions(UUID roleId)`
- [ ] 4.2 Create `JooqRoleRepository.java` implementation
- [ ] 4.3 Implement all CRUD methods
- [ ] 4.4 Implement assignPermission (insert into role_permission table)
- [ ] 4.5 Implement removePermission
- [ ] 4.6 Implement findPermissions with join query
- [ ] 4.7 Add exception handling
- [ ] 4.8 Write unit tests

## 5. PermissionRepository Implementation
- [ ] 5.1 Create `PermissionRepository.java` interface with methods:
  - `findById(UUID id)`
  - `findByResourceAction(UUID realmId, String resource, String action)`
  - `findAll(UUID realmId, int offset, int limit)`
  - `create(Permission permission)`
  - `update(Permission permission)`
  - `delete(UUID id)`
  - `findByPolicy(UUID policyId)`
- [ ] 5.2 Create `JooqPermissionRepository.java` implementation
- [ ] 5.3 Implement all CRUD methods
- [ ] 5.4 Implement findByResourceAction with composite unique constraint
- [ ] 5.5 Implement findByPolicy with join query
- [ ] 5.6 Add exception handling
- [ ] 5.7 Write unit tests

## 6. PolicyRepository Implementation
- [ ] 6.1 Create `PolicyRepository.java` interface with methods:
  - `findById(UUID id)`
  - `findByType(UUID realmId, PolicyType type)`
  - `findAll(UUID realmId, int offset, int limit)`
  - `create(Policy policy)`
  - `update(Policy policy)`
  - `delete(UUID id)`
- [ ] 6.2 Create `JooqPolicyRepository.java` implementation
- [ ] 6.3 Implement all CRUD methods
- [ ] 6.4 Implement findByType
- [ ] 6.5 Add expression validation (optional in this phase)
- [ ] 6.6 Add exception handling
- [ ] 6.7 Write unit tests

## 7. ClientRepository Implementation
- [ ] 7.1 Update `ClientRepository.java` interface with complete methods:
  - `findById(UUID id)`
  - `findByClientId(UUID realmId, String clientId)`
  - `findAll(UUID realmId, int offset, int limit)`
  - `create(Client client)`
  - `update(Client client)`
  - `delete(UUID id)`
  - `authenticate(String clientId, String clientSecret)`
- [ ] 7.2 Create `JooqClientRepository.java` implementation
- [ ] 7.3 Implement all CRUD methods
- [ ] 7.4 Implement findByClientId (unique constraint)
- [ ] 7.5 Implement authenticate with BCrypt verification
- [ ] 7.6 Add exception handling
- [ ] 7.7 Write unit tests

## 8. TokenRepository Implementation
- [ ] 8.1 Create `TokenRepository.java` interface with methods:
  - `findById(UUID id)`
  - `findByUserId(UUID userId, TokenType type)`
  - `findByClientId(UUID clientId, TokenType type)`
  - `create(Token token)`
  - `revoke(UUID id)`
  - `revokeByUser(UUID userId)`
  - `deleteExpired(LocalDateTime before)`
- [ ] 8.2 Create `JooqTokenRepository.java` implementation
- [ ] 8.3 Implement findById
- [ ] 8.4 Implement findByUserId with type filter
- [ ] 8.5 Implement findByClientId with type filter
- [ ] 8.6 Implement create
- [ ] 8.7 Implement revoke (mark as revoked or delete)
- [ ] 8.8 Implement revokeByUser
- [ ] 8.9 Implement deleteExpired for cleanup job
- [ ] 8.10 Add exception handling
- [ ] 8.11 Write unit tests

## 9. AuditLogRepository Implementation
- [ ] 9.1 Create `AuditLogRepository.java` interface with methods:
  - `findById(Long id)`
  - `create(AuditLog log)`
  - `findByUser(UUID userId, LocalDateTime start, LocalDateTime end)`
  - `findByEvent(AuditEventType type, LocalDateTime start, LocalDateTime end)`
  - `findAll(UUID realmId, LocalDateTime start, LocalDateTime end, int offset, int limit)`
- [ ] 9.2 Create `JooqAuditLogRepository.java` implementation
- [ ] 9.3 Implement all methods
- [ ] 9.4 Implement date range queries with proper indexing
- [ ] 9.5 Add exception handling
- [ ] 9.6 Write unit tests

## 10. Testing and Validation
- [ ] 10.1 Create test schema initialization script (H2 database)
- [ ] 10.2 Configure test application-test.yml for H2
- [ ] 10.3 Write unit tests for each repository
- [ ] 10.4 Test coverage > 90% for repository layer
- [ ] 10.5 Run all tests: `mvn test`
- [ ] 10.6 Verify no compilation errors: `mvn clean compile`
- [ ] 10.7 Manual integration testing with PostgreSQL

## 11. Documentation
- [ ] 11.1 Add Javadoc comments to all repository interfaces
- [ ] 11.2 Add implementation notes for complex queries
- [ ] 11.3 Update DEVELOPMENT_PLAN.md with completion status
- [ ] 11.4 Record any database index requirements for performance

## 12. Service Layer Implementation ✅ (Completed)
### 12.1 RealmService
- [x] 12.1.1 Create `RealmService.java` interface with business methods:
  - `createRealm(String name, boolean enabled)`
  - `findRealmById(UUID id)`
  - `findRealmByName(String name)`
  - `findAllRealms(int page, int size)`
  - `updateRealmName(UUID id, String name)`
  - `enableRealm(UUID id)`
  - `disableRealm(UUID id)`
  - `isRealmActive(UUID id)`
  - `getActiveRealmCount()`
- [x] 12.1.2 Create `RealmServiceImpl.java` implementation
- [x] 12.1.3 Integrate with RealmRepository for data access
- [x] 12.1.4 Add exception handling and validation
- [x] 12.1.5 Add business logic validation (e.g., realm name uniqueness)

### 12.2 UserService
- [x] 12.2.1 Create `UserService.java` interface with business methods:
  - `createUser(UUID realmId, String username, String email, String passwordHash)`
  - `findUserById(UUID id)`
  - `findUserByUsername(UUID realmId, String username)`
  - `findUserByEmail(UUID realmId, String email)`
  - `findAllUsers(UUID realmId, int page, int size)`
  - `updateUser(User user)`
  - `changePassword(UUID userId, String newPasswordHash)`
  - `disableUser(UUID userId)`
  - `enableUser(UUID userId)`
  - `assignRole(UUID userId, UUID roleId)`
  - `removeRole(UUID userId, UUID roleId)`
  - `getUserRoles(UUID userId)`
  - `getUserPermissions(UUID userId)`
  - `hasPermission(UUID userId, String permission)`
  - `isUserActive(UUID userId)`
- [x] 12.2.2 Create `UserServiceImpl.java` implementation
- [x] 12.2.3 Integrate with UserRepository for data access
- [x] 12.2.4 Implement role-permission inheritance logic
- [x] 12.2.5 Add password handling integration
- [x] 12.2.6 Add exception handling and validation

## 13. Controller Layer Implementation ✅ (Completed)
### 13.1 DTOs
- [x] 13.1.1 Create `RealmDTO.java` with validation annotations
- [x] 13.1.2 Create `UserDTO.java` with validation annotations
- [x] 13.1.3 Add Jakarta Validation dependency to pom.xml
- [x] 13.1.4 Add Spring Web dependency to pom.xml

### 13.2 Exception Handling
- [x] 13.2.1 Create `ResourceNotFoundException.java`
- [x] 13.2.2 Create `ErrorResponse.java` with field errors support
- [x] 13.2.3 Create `GlobalExceptionHandler.java` with @ControllerAdvice
- [x] 13.2.4 Handle validation errors with proper error response format

### 13.3 RealmController
- [x] 13.3.1 Create `RealmController.java` REST controller
- [x] 13.3.2 Implement POST /api/realms (create realm)
- [x] 13.3.3 Implement GET /api/realms/{id} (get realm by ID)
- [x] 13.3.4 Implement GET /api/realms/name/{name} (get by name)
- [x] 13.3.5 Implement GET /api/realms (list all with pagination)
- [x] 13.3.6 Implement PUT /api/realms/{id} (update realm)
- [x] 13.3.7 Implement POST /api/realms/{id}/enable
- [x] 13.3.8 Implement POST /api/realms/{id}/disable
- [x] 13.3.9 Implement DELETE /api/realms/{id}
- [x] 13.3.10 Implement GET /api/realms/count

### 13.4 UserController
- [x] 13.4.1 Create `UserController.java` REST controller
- [x] 13.4.2 Implement POST /api/users (create user)
- [x] 13.4.3 Implement GET /api/users/{id} (get user by ID)
- [x] 13.4.4 Implement GET /api/users/username/{username}
- [x] 13.4.5 Implement GET /api/users/email/{email}
- [x] 13.4.6 Implement GET /api/users/realm/{realmId} (list by realm)
- [x] 13.4.7 Implement GET /api/users (list all with pagination)
- [x] 13.4.8 Implement PUT /api/users/{id} (update user)
- [x] 13.4.9 Implement POST /api/users/{id}/enable
- [x] 13.4.10 Implement POST /api/users/{id}/disable
- [x] 13.4.11 Implement DELETE /api/users/{id}
- [x] 13.4.12 Implement POST /api/users/{userId}/roles/{roleId}
- [x] 13.4.13 Implement DELETE /api/users/{userId}/roles/{roleId}
- [x] 13.4.14 Implement GET /api/users/{userId}/roles
- [x] 13.4.15 Implement GET /api/users/{userId}/permissions
- [x] 13.4.16 Implement GET /api/users/{userId}/permissions/{permission}
- [x] 13.4.17 Implement GET /api/users/realm/{realmId}/count

### 13.5 Controller Best Practices
- [x] 13.5.1 Use proper HTTP status codes (201, 204, 404, etc.)
- [x] 13.5.2 Add @Valid for request body validation
- [x] 13.5.3 Use @ControllerAdvice for global exception handling
- [x] 13.5.4 Implement proper error response format
- [x] 13.5.5 Add pagination support for list endpoints
- [ ] 13.5.6 Add authentication/authorization annotations
- [ ] 13.5.7 Add API documentation (OpenAPI/Swagger)

### 13.6 Service Layer Refactoring (DTO-based)
- [x] 13.6.1 Update RealmService interface to use RealmDTO
- [x] 13.6.2 Update RealmServiceImpl to work with RealmDTO
- [x] 13.6.3 Update UserService interface to use UserDTO
- [x] 13.6.4 Update UserServiceImpl to work with UserDTO

## 14. In-Memory Repository Implementation ✅ (Completed)
### 14.1 InMemoryRealmRepository
- [x] 14.1.1 Create `InMemoryRealmRepository.java` implementation
- [x] 14.1.2 Implement all CRUD methods
- [x] 14.1.3 Add pagination support
- [x] 14.1.4 Add enable/disable functionality
- [x] 14.1.5 Write 10 unit tests

### 14.2 InMemoryUserRepository
- [x] 14.2.1 Create `InMemoryUserRepository.java` implementation
- [x] 14.2.2 Implement all CRUD methods
- [x] 14.2.3 Implement role assignment/removal
- [x] 14.2.4 Implement permission lookup
- [x] 14.2.5 Write 11 unit tests

### 14.3 InMemoryRoleRepository
- [x] 14.3.1 Create `InMemoryRoleRepository.java` implementation
- [x] 14.3.2 Implement all CRUD methods
- [x] 14.3.3 Implement permission assignment/removal
- [x] 14.3.4 Write 10 unit tests

## 15. RoleRepository Implementation ✅ (Completed)
- [x] 15.1 Update `RoleRepository.java` interface with complete method signatures
- [x] 15.2 Create `Role.java` domain entity
- [x] 15.3 Create `InMemoryRoleRepository.java` implementation
- [x] 15.4 Implement findById, findByName, findAll
- [x] 15.5 Implement create, update, delete
- [x] 15.6 Implement permission management (assign/remove/find)
- [x] 15.7 Add unit tests

## 16. Service Layer Integration ✅ (Completed)
### 16.1 RealmService Integration
- [x] 16.1.1 Update `RealmServiceImpl.java` to use `InMemoryRealmRepository`
- [x] 16.1.2 Add domain entity to DTO mapping
- [x] 16.1.3 Verify compilation and tests

### 16.2 UserService Integration
- [x] 16.2.1 Update `UserServiceImpl.java` to use `InMemoryUserRepository`
- [x] 16.2.2 Add domain entity to DTO mapping
- [x] 16.2.3 Verify compilation and tests

### 16.3 User Entity Enhancement
- [x] 16.3.1 Add firstName and lastName fields to User entity
- [x] 16.3.2 Update getters and setters

## 17. RoleController Implementation ✅ (Completed)
- [x] 17.1 Create `RoleController.java` REST controller
- [x] 17.2 Implement POST /api/roles (create role)
- [x] 17.3 Implement GET /api/roles/{id} (get role by ID)
- [x] 17.4 Implement GET /api/roles/realm/{realmId}/name/{name}
- [x] 17.5 Implement GET /api/roles/realm/{realmId} (list by realm)
- [x] 17.6 Implement GET /api/roles (list all)
- [x] 17.7 Implement PUT /api/roles/{id} (update role)
- [x] 17.8 Implement DELETE /api/roles/{id} (delete role)
- [x] 17.9 Implement GET /api/roles/{id}/permissions
- [x] 17.10 Implement POST /api/roles/{roleId}/permissions/{permissionId}
- [x] 17.11 Implement DELETE /api/roles/{roleId}/permissions/{permissionId}
- [x] 17.12 Implement GET /api/roles/realm/{realmId}/count

## 18. Security Configuration ✅ (Completed)
### 18.1 JWT Dependencies
- [x] 18.1.1 Add JJWT dependencies (jjwt-api, jjwt-impl, jjwt-jackson) to pom.xml

### 18.2 Security Configuration
- [x] 18.2.1 Create `SecurityConfig.java` with stateless session management
- [x] 18.2.2 Configure public endpoints (/api/auth/**, /api/health, /actuator/**)
- [x] 18.2.3 Configure protected endpoints (all others require authentication)
- [x] 18.2.4 Add JWT authentication filter to filter chain
- [x] 18.2.5 Configure BCrypt password encoder

### 18.3 JWT Token Provider
- [x] 18.3.1 Create `JwtTokenProvider.java` utility class
- [x] 18.3.2 Implement token generation for authenticated users
- [x] 18.3.3 Implement username extraction from JWT tokens
- [x] 18.3.4 Implement token validation with proper error handling
- [x] 18.3.5 Configure JWT secret and expiration from application.yml

### 18.4 JWT Authentication Filter
- [x] 18.4.1 Create `JwtAuthenticationFilter.java`
- [x] 18.4.2 Extract JWT token from Authorization header
- [x] 18.4.3 Validate token and set authentication context
- [x] 18.4.4 Handle expired and invalid tokens properly

### 18.5 Authentication Entry Point
- [x] 18.5.1 Create `JwtAuthenticationEntryPoint.java`
- [x] 18.5.2 Handle unauthorized requests with proper 401 response

### 18.6 User Details Service
- [x] 18.6.1 Create `CustomUserDetailsService.java`
- [x] 18.6.2 Load user details from UserRepository
- [x] 18.6.3 Integrate with Spring Security authentication

### 18.7 AuthController
- [x] 18.7.1 Create `LoginRequest.java` DTO
- [x] 18.7.2 Create `RegisterRequest.java` DTO
- [x] 18.7.3 Create `AuthResponse.java` DTO
- [x] 18.7.4 Create `AuthService.java` interface
- [x] 18.7.5 Create `AuthServiceImpl.java` implementation
- [x] 18.7.6 Create `AuthController.java` REST controller
- [x] 18.7.7 Implement POST /api/auth/login
- [x] 18.7.8 Implement POST /api/auth/register
- [x] 18.7.9 Implement POST /api/auth/refresh

### 18.8 Password Encoding Integration
- [x] 18.8.1 Update `UserServiceImpl.java` to use BCryptPasswordEncoder
- [x] 18.8.2 Encode passwords during user creation
- [x] 18.8.3 Update `AuthServiceImpl.java` to use passwordEncoder.matches()
- [x] 18.8.4 Verify password verification works correctly

### 18.9 Health Controller
- [x] 18.9.1 Create `HealthController.java`
- [x] 18.9.2 Implement GET /api/health endpoint

### 18.10 Integration Testing
- [x] 18.10.1 Test health endpoint
- [x] 18.10.2 Test user registration with JWT token generation
- [x] 18.10.3 Test user login with BCrypt password verification
- [x] 18.10.4 Test protected endpoint access with JWT token
- [x] 18.10.5 Test unauthorized access to protected endpoints
- [x] 18.10.6 All 31 unit tests pass
- [x] 18.10.7 Application compiles successfully
- [x] 18.10.8 Application starts and runs correctly

## Completion Criteria
- All repository interfaces have complete method signatures
- All repository implementations are complete and tested
- Test coverage > 90%
- No compilation errors
- All manual integration tests pass
- Code follows project conventions (openspec/project.md)

## 19. Database Integration ✅ (Completed)
### 19.1 Liquibase Configuration
- [x] 19.1.1 Add spring-boot-starter-liquibase dependency to pom.xml
- [x] 19.1.2 Configure liquibase settings in application.yml
- [x] 19.1.3 Create master.xml changelog file
- [x] 19.1.4 Configure change-log path

### 19.2 Database Schema
- [x] 19.2.1 Create V1__Initial_Schema.sql migration script
- [x] 19.2.2 Create ima_realm table with proper indexes
- [x] 19.2.3 Create ima_user table with realm association
- [x] 19.2.4 Create ima_role and ima_permission tables
- [x] 19.2.5 Create association tables (user_role, role_permission)
- [x] 19.2.6 Create support tables (refresh_token, audit_log, client, policy)
- [x] 19.2.7 Add database enums for token_type and audit_event_type
- [x] 19.2.8 Add proper foreign key constraints and indexes
- [x] 19.2.9 Add table comments for documentation

### 19.3 Docker Setup
- [x] 19.3.1 Create docker-compose.postgres.yml
- [x] 19.3.2 Configure PostgreSQL container with proper volumes
- [x] 19.3.3 Set up health checks for database readiness
- [x] 19.3.4 Create scripts/setup-database.sh for manual setup

### 19.4 Documentation
- [x] 19.4.1 Create comprehensive DATABASE_SETUP.md
- [x] 19.4.2 Document quick start with Docker
- [x] 19.4.3 Document manual setup procedures
- [x] 19.4.4 Include database schema documentation
- [x] 19.4.5 Add troubleshooting section
- [x] 19.4.6 Include performance considerations

### 19.5 Testing
- [x] 19.5.1 Verify compilation with Liquibase dependency
- [x] 19.5.2 Run unit tests (31/31 passing)
- [x] 19.5.3 Verify clean build with mvn clean compile
- [x] 19.5.4 Test application startup with database connection
