## ADDED Requirements

### Requirement: Realm Repository Operations
The system SHALL provide RealmRepository with CRUD operations for managing realms within a multi-tenant environment.

#### Scenario: Create realm
- **GIVEN** a valid realm configuration with unique name within scope
- **WHEN** creating a new realm
- **THEN** realm is persisted with auto-generated UUID and enabled status set to TRUE
- **AND** creation timestamp is automatically set
- **AND** the created realm can be retrieved by ID

#### Scenario: Find realm by ID
- **GIVEN** a realm exists with specific UUID
- **WHEN** querying for realm by ID
- **THEN** returns Optional containing the realm
- **OR** returns Optional.empty if realm does not exist

#### Scenario: Find realm by name
- **GIVEN** a realm exists with name "my-realm" within specific realmId scope
- **WHEN** querying for realm by name
- **THEN** returns Optional containing the realm
- **AND** the realm matches the provided name and realmId

#### Scenario: Enable realm
- **GIVEN** a realm exists with enabled = FALSE
- **WHEN** enabling the realm by ID
- **THEN** the realm's enabled status is updated to TRUE
- **AND** the update is persisted to database

#### Scenario: Disable realm
- **GIVEN** a realm exists with enabled = TRUE
- **WHEN** disabling the realm by ID
- **THEN** the realm's enabled status is updated to FALSE
- **AND** the update is persisted to database

---

### Requirement: User Repository Operations
The system SHALL provide UserRepository for user management with authentication and authorization support, including role assignments and permission lookups.

#### Scenario: Find user by username
- **GIVEN** a realmId and username "john.doe"
- **WHEN** querying for user by username within realm
- **THEN** returns Optional<User> containing the user with all associated data
- **OR** returns Optional.empty if user does not exist in the realm

#### Scenario: Find user by email
- **GIVEN** a realmId and email "john@example.com"
- **WHEN** querying for user by email within realm
- **THEN** returns Optional<User> containing the user
- **OR** returns Optional.empty if email does not exist in the realm

#### Scenario: Create user with password hashing
- **GIVEN** a user object with plain text password
- **WHEN** creating a new user
- **THEN** password is hashed using BCrypt before storage
- **AND** user is persisted with auto-generated UUID
- **AND** user is associated with the provided realmId
- **AND** username is unique within the realm

#### Scenario: Assign role to user
- **GIVEN** a userId and roleId
- **WHEN** assigning a role to user
- **THEN** a new record is created in user_role table
- **AND** the mapping is persisted to database
- **AND** the same role cannot be assigned twice to the same user (unique constraint)

#### Scenario: Remove role from user
- **GIVEN** a userId with an assigned roleId
- **WHEN** removing the role from user
- **THEN** the record is deleted from user_role table
- **AND** the removal is persisted to database

#### Scenario: Find user permissions
- **GIVEN** a userId with multiple roles
- **AND** each role has multiple permissions
- **WHEN** querying for all user permissions
- **THEN** returns list of all permission identifiers from all user's roles
- **AND** duplicates are removed
- **AND** permissions are derived through role -> permission relationship

#### Scenario: Find user roles
- **GIVEN** a userId with multiple assigned roles
- **WHEN** querying for user roles
- **THEN** returns list of all role identifiers assigned to the user
- **AND** roles are retrieved from user_role table

#### Scenario: Update user
- **GIVEN** an existing user with changes to email or attributes
- **WHEN** updating the user
- **THEN** the changes are persisted to database
- **AND** password hash is not changed if not provided

#### Scenario: Soft delete user
- **GIVEN** an existing userId
- **WHEN** deleting the user
- **THEN** the user record is removed from database
- **AND** all associated records in user_role are also removed (cascade)

#### Scenario: List users with pagination
- **GIVEN** a realmId with multiple users
- **WHEN** querying users with offset=10 and limit=20
- **THEN** returns up to 20 users starting from the 11th record
- **AND** users are ordered by creation date (newest first)

---

### Requirement: Role Repository Operations
The system SHALL provide RoleRepository for role management with permission assignment capabilities.

#### Scenario: Create role
- **GIVEN** a realmId and role details with unique name within realm
- **WHEN** creating a new role
- **THEN** role is persisted with auto-generated UUID
- **AND** role is associated with the provided realmId
- **AND** creation timestamp is automatically set

#### Scenario: Find role by name
- **GIVEN** a realmId and role name "ADMIN"
- **WHEN** querying for role by name within realm
- **THEN** returns Optional containing the role
- **OR** returns Optional.empty if role does not exist in the realm

#### Scenario: Assign permission to role
- **GIVEN** a roleId and permissionId
- **WHEN** assigning a permission to role
- **THEN** a new record is created in role_permission table
- **AND** the mapping is persisted to database
- **AND** the same permission cannot be assigned twice to the same role

#### Scenario: Remove permission from role
- **GIVEN** a roleId with an assigned permissionId
- **WHEN** removing the permission from role
- **THEN** the record is deleted from role_permission table

#### Scenario: Find role permissions
- **GIVEN** a roleId with multiple assigned permissions
- **WHEN** querying for role permissions
- **THEN** returns list of all permission identifiers assigned to the role
- **AND** permissions are retrieved from role_permission table

#### Scenario: Update role
- **GIVEN** an existing role with changes to description
- **WHEN** updating the role
- **THEN** the changes are persisted to database
- **AND** role name cannot be changed to existing name in same realm

#### Scenario: Delete role
- **GIVEN** an existing roleId
- **WHEN** deleting the role
- **THEN** the role record is removed from database
- **AND** all associated records in role_permission are removed (cascade)
- **AND** all associated records in user_role are removed (cascade)

#### Scenario: List roles with pagination
- **GIVEN** a realmId with multiple roles
- **WHEN** querying roles with offset=0 and limit=10
- **THEN** returns up to 10 roles starting from first record
- **AND** roles are ordered by creation date (newest first)

---

### Requirement: Permission Repository Operations
The system SHALL provide PermissionRepository for permission management with resource-action granularity.

#### Scenario: Create permission
- **GIVEN** a realmId, resource "users", and action "read"
- **WHEN** creating a new permission
- **THEN** permission is persisted with auto-generated UUID
- **AND** permission is associated with the provided realmId
- **AND** combination of realmId, resource, and action is unique

#### Scenario: Find permission by resource and action
- **GIVEN** a realmId, resource "users", and action "write"
- **WHEN** querying for permission
- **THEN** returns Optional containing the matching permission
- **OR** returns Optional.empty if no matching permission exists

#### Scenario: List permissions with pagination
- **GIVEN** a realmId with multiple permissions
- **WHEN** querying permissions with offset=0 and limit=50
- **THEN** returns up to 50 permissions starting from first record
- **AND** permissions are ordered by resource, then action

#### Scenario: Find permissions by policy
- **GIVEN** a policyId with multiple associated permissions
- **WHEN** querying for permissions by policy
- **THEN** returns list of all permissions linked to the policy
- **AND** permissions are retrieved from permission_policy table

#### Scenario: Update permission
- **GIVEN** an existing permission
- **WHEN** updating the permission
- **THEN** the changes are persisted to database
- **AND** resource-action combination must remain unique

#### Scenario: Delete permission
- **GIVEN** an existing permissionId
- **WHEN** deleting the permission
- **THEN** the permission record is removed from database
- **AND** all associated records in role_permission are removed (cascade)
- **AND** all associated records in permission_policy are removed (cascade)

---

### Requirement: Policy Repository Operations
The system SHALL provide PolicyRepository for policy management supporting role, attribute, time, and custom policy types.

#### Scenario: Create policy
- **GIVEN** a realmId, policy type "ROLE", and expression "user.roles.contains('ADMIN')"
- **WHEN** creating a new policy
- **THEN** policy is persisted with auto-generated UUID
- **AND** policy is associated with the provided realmId
- **AND** creation timestamp is automatically set

#### Scenario: Find policies by type
- **GIVEN** a realmId and policy type "ATTRIBUTE"
- **WHEN** querying for policies by type
- **THEN** returns list of all policies of that type within the realm
- **AND** each policy contains expression and description

#### Scenario: Update policy
- **GIVEN** an existing policy
- **WHEN** updating the policy expression or description
- **THEN** the changes are persisted to database

#### Scenario: Delete policy
- **GIVEN** an existing policyId
- **WHEN** deleting the policy
- **THEN** the policy record is removed from database
- **AND** all associated records in permission_policy are removed (cascade)

#### Scenario: List policies with pagination
- **GIVEN** a realmId with multiple policies
- **WHEN** querying policies with offset=0 and limit=20
- **THEN** returns up to 20 policies starting from first record
- **AND** policies are ordered by type, then creation date

---

### Requirement: Client Repository Operations
The system SHALL provide ClientRepository for OAuth2 client management with authentication and configuration support.

#### Scenario: Find client by client_id
- **GIVEN** a realmId and client_id "my-webapp"
- **WHEN** querying for client by client_id within realm
- **THEN** returns Optional containing the client
- **OR** returns Optional.empty if client does not exist in the realm

#### Scenario: Create confidential client
- **GIVEN** a realmId, client_id, and client_type "CONFIDENTIAL"
- **WHEN** creating a new client
- **THEN** client is persisted with auto-generated UUID
- **AND** client_secret is automatically generated and BCrypt hashed
- **AND** client_id is unique within the realm

#### Scenario: Create public client
- **GIVEN** a realmId, client_id, and client_type "PUBLIC"
- **WHEN** creating a new client
- **THEN** client is persisted without client_secret
- **AND** client_id is unique within the realm

#### Scenario: Authenticate client
- **GIVEN** a client_id "my-webapp" and correct client_secret
- **WHEN** authenticating the client
- **THEN** returns Optional containing the client
- **AND** BCrypt verification succeeds

#### Scenario: Authentication with wrong secret
- **GIVEN** a client_id "my-webapp" and incorrect client_secret
- **WHEN** authenticating the client
- **THEN** returns Optional.empty

#### Scenario: Update client
- **GIVEN** an existing client
- **WHEN** updating redirect_uris or scopes
- **THEN** the changes are persisted to database
- **AND** client_secret is regenerated if explicitly requested

#### Scenario: Delete client
- **GIVEN** an existing clientId
- **WHEN** deleting the client
- **THEN** the client record is removed from database
- **AND** all associated tokens are revoked or marked as invalid

#### Scenario: List clients with pagination
- **GIVEN** a realmId with multiple clients
- **WHEN** querying clients with offset=0 and limit=10
- **THEN** returns up to 10 clients starting from first record
- **AND** clients are ordered by creation date

---

### Requirement: Token Repository Operations
The system SHALL provide TokenRepository for JWT token lifecycle management including issuance, revocation, and cleanup.

#### Scenario: Create access token
- **GIVEN** userId, clientId, tokenType ACCESS_TOKEN, and expiresAt
- **WHEN** creating a new token
- **THEN** token is persisted with auto-generated UUID
- **AND** token is associated with user, client, and realm
- **AND** expiresAt and createdAt are stored correctly

#### Scenario: Find tokens by user
- **GIVEN** a userId and tokenType REFRESH_TOKEN
- **WHEN** querying for tokens by user and type
- **THEN** returns list of all refresh tokens for that user
- **AND** only non-expired tokens are returned

#### Scenario: Revoke token
- **GIVEN** a valid tokenId
- **WHEN** revoking the token
- **THEN** the token is marked as revoked or deleted from database
- **AND** the token can no longer be used for authentication

#### Scenario: Revoke all user tokens
- **GIVEN** a userId with multiple active tokens
- **WHEN** revoking all tokens for the user
- **THEN** all tokens for that user are marked as revoked or deleted
- **AND** user must re-authenticate to get new tokens

#### Scenario: Delete expired tokens
- **GIVEN** tokens with expiresAt before current timestamp
- **WHEN** running cleanup job to delete expired tokens
- **THEN** all expired tokens are removed from database
- **AND** only expired tokens are affected

#### Scenario: Find tokens by client
- **GIVEN** a clientId and tokenType ACCESS_TOKEN
- **WHEN** querying for tokens by client and type
- **THEN** returns list of all access tokens issued to that client
- **AND** tokens are ordered by creation date (newest first)

---

### Requirement: Audit Log Repository Operations
The system SHALL provide AuditLogRepository for security event logging including login, logout, password changes, and role changes.

#### Scenario: Create audit log entry
- **GIVEN** audit event details (eventType, subjectId, subjectType, ipAddress, detail)
- **WHEN** creating a new audit log entry
- **THEN** audit log is persisted with auto-generated ID
- **AND** creation timestamp is automatically set
- **AND** realmId is stored if provided

#### Scenario: Find audit logs by user
- **GIVEN** a userId and date range (start to end)
- **WHEN** querying for audit logs for that user
- **THEN** returns list of all audit logs for the user within date range
- **AND** logs are ordered by creation date (most recent first)

#### Scenario: Find audit logs by event type
- **GIVEN** an AuditEventType LOGIN and date range
- **WHEN** querying for audit logs by event type
- **THEN** returns list of all login events within date range
- **AND** logs include user, IP address, and timestamp

#### Scenario: Find audit logs with pagination
- **GIVEN** a realmId, date range, offset=0, and limit=100
- **WHEN** querying for audit logs with pagination
- **THEN** returns up to 100 audit logs starting from first record
- **AND** logs are ordered by creation date (most recent first)

#### Scenario: Audit log for password change
- **GIVEN** a userId changing password
- **WHEN** logging the password change event
- **THEN** audit log is created with eventType PASSWORD_CHANGE
- **AND** subjectId is the userId
- **AND** subjectType is USER
- **AND** detail contains information about the change (not the password itself)

#### Scenario: Audit log for role change
- **GIVEN** a userId with role assignment change
- **WHEN** logging the role change event
- **THEN** audit log is created with eventType ROLE_CHANGE
- **AND** detail contains oldRoles and newRoles lists
- **AND** timestamp reflects when the change occurred

---

### Requirement: Repository Error Handling
All repository implementations SHALL throw appropriate exceptions for error conditions.

#### Scenario: Resource not found
- **GIVEN** a non-existent resourceId
- **WHEN** attempting to update or delete the resource
- **THEN** throws ResourceNotFoundException with descriptive message
- **AND** exception includes resource type and ID

#### Scenario: Unique constraint violation
- **GIVEN** a duplicate value for a unique field (e.g., username in same realm)
- **WHEN** attempting to create or update
- **THEN** throws DuplicateResourceException with descriptive message
- **AND** exception includes field name and conflicting value

#### Scenario: Invalid input
- **GIVEN** null or invalid parameters (e.g., null realmId)
- **WHEN** calling any repository method
- **THEN** throws IllegalArgumentException or ValidationException
- **AND** exception includes specific validation error message

---

### Requirement: Repository Performance
All repository implementations SHALL follow performance best practices for database operations.

#### Scenario: Use indexed columns in queries
- **GIVEN** queries filtering by indexed columns (e.g., realm_id, username)
- **WHEN** executing the query
- **THEN** database query uses index for efficient lookup
- **AND** query execution time is optimal (verified through EXPLAIN)

#### Scenario: Batch operations
- **GIVEN** multiple records to insert or update
- **WHEN** performing batch operations
- **THEN** operations use JOOQ batch API for efficiency
- **AND** multiple records are processed in single database transaction

#### Scenario: Pagination limits
- **GIVEN** a query returning potentially large result sets
- **WHEN** querying with pagination (offset, limit)
- **THEN** limit parameter enforces maximum returned records
- **AND** offset parameter skips specified number of records
- **AND** default limit is applied if not provided (e.g., 100)
