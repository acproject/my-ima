# Database Setup Guide

This guide covers setting up the PostgreSQL database for the MY-IMA IAM Server.

## Quick Start with Docker

### 1. Start PostgreSQL Container

```bash
docker-compose -f docker-compose.postgres.yml up -d
```

This will:
- Create a PostgreSQL 16 container
- Initialize the `my_ima` database
- Set up the database schema using Liquibase migrations

### 2. Verify Database is Ready

```bash
docker exec -it my-ima-postgres psql -U postgres -d my_ima -c "\dt"
```

You should see the tables created by the migration script.

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will automatically connect to the database and run any pending migrations.

## Manual Setup

### 1. Install PostgreSQL

- **macOS**: `brew install postgresql`
- **Ubuntu/Debian**: `sudo apt-get install postgresql`
- **Windows**: Download from postgresql.org

### 2. Create Database

```bash
# Create database
export PGPASSWORD=ac123456
psql -U postgres -h localhost -c "CREATE DATABASE my_ima;"

# Or use the setup script
chmod +x scripts/setup-database.sh
./scripts/setup-database.sh
```

### 3. Run Migrations

The application will automatically run migrations on startup. To run them manually:

```bash
# Using Liquibase command line (if installed)
liquibase update

# Or just start the application
mvn spring-boot:run
```

## Database Schema

The database includes the following tables:

### Core Tables
- **ima_realm**: Multi-tenant realm configuration
- **ima_user**: User accounts with realm association
- **ima_role**: Role definitions per realm
- **ima_permission**: Permission definitions per realm

### Association Tables
- **ima_user_role**: User-Role assignments
- **ima_role_permission**: Role-Permission assignments
- **ima_permission_policy**: Policy associations

### Support Tables
- **ima_refresh_token**: JWT refresh tokens
- **ima_audit_log**: Security audit trail
- **ima_client**: OAuth2/OIDC client applications
- **ima_policy**: Authorization policies

## Configuration

Database configuration is managed in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/my_ima
    username: postgres
    password: ac123456
    driver-class-name: org.postgresql.Driver
  liquibase:
    enabled: true
    change-log: classpath:db/migration/master.xml
```

## Development Notes

### Reset Database

To reset the database (WARNING: deletes all data):

```bash
# Stop the application
docker-compose -f docker-compose.postgres.yml down -v

# Remove volume
docker volume rm my-ima_postgres_data

# Restart
docker-compose -f docker-compose.postgres.yml up -d
```

### Database Credentials

For development:
- **Host**: localhost:5432
- **Database**: my_ima
- **Username**: postgres
- **Password**: ac123456

For production, use environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-server:5432/my_ima
export SPRING_DATASOURCE_USERNAME=secure_user
export SPRING_DATASOURCE_PASSWORD=strong_password_here
```

### Testing Migrations

To test migrations without running the full application:

```bash
# Use Maven to run Liquibase
mvn liquibase:update
```

## Performance Considerations

### Indexes

The migration script creates indexes for common query patterns:
- Realm lookups by name
- User lookups by username, email, and realm
- Role lookups by realm
- Audit log queries by timestamp and user

### Connection Pool

Spring Boot uses HikariCP for connection pooling. Default settings work for development. For production, tune these in `application.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

## Troubleshooting

### Connection Refused

```
FATAL: password authentication failed for user "postgres"
```

**Solution**: Verify password or reset PostgreSQL authentication.

### Database Already Exists

```
ERROR: database "my_ima" already exists
```

**Solution**: This is normal. The database was created previously.

### Migration Failures

```
liquibase.exception.MigrationFailedException: Migration failed
```

**Solution**: 
1. Check that PostgreSQL is running
2. Verify connection settings in `application.yml`
3. Check logs for specific error messages

## Next Steps

After database setup:
1. Run the application: `mvn spring-boot:run`
2. Test health endpoint: `curl http://localhost:8086/api/health`
3. Register a user: `POST /api/auth/register`
4. Login: `POST /api/auth/login`
