#!/bin/bash
# Database setup script for MY-IMA IAM Server
# This script creates the database and runs initial migrations

set -e

echo "=== MY-IMA Database Setup ==="
echo "Creating database 'my-ima' if it doesn't exist..."

# Create database using createdb command or direct SQL
export PGPASSWORD=${PGPASSWORD:-ac123456}

# Try to create the database
psql -U postgres -h localhost -c "CREATE DATABASE my_ima;" 2>/dev/null || echo "Database 'my_ima' already exists or creation failed"

echo "Database setup complete!"
echo ""
echo "To run the application with database migrations:"
echo "  1. Make sure PostgreSQL is running"
echo "  2. Run: mvn spring-boot:run"
echo ""
echo "The application will automatically run Liquibase migrations on startup."
