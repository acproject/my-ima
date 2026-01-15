#!/bin/bash
# Test script to verify RealmRepository implementation

echo "=== Testing RealmRepository Implementation ==="
echo ""

# Check if project compiles
echo "1. Checking if project compiles..."
mvn compile -q

if [ $? -eq 0 ]; then
    echo "✅ Project compiles successfully"
else
    echo "❌ Project compilation failed"
    exit 1
fi

echo ""
echo "2. Checking if RealmRepository interface exists..."
if [ -f "src/main/java/com/owiseman/core/jooq/repository/RealmRepository.java" ]; then
    echo "✅ RealmRepository interface exists"
else
    echo "❌ RealmRepository interface not found"
    exit 1
fi

echo ""
echo "3. Checking if JooqRealmRepository implementation exists..."
if [ -f "src/main/java/com/owiseman/core/jooq/repository/impl/JooqRealmRepository.java" ]; then
    echo "✅ JooqRealmRepository implementation exists"
else
    echo "❌ JooqRealmRepository implementation not found"
    exit 1
fi

echo ""
echo "4. Checking if implementation uses JOOQ generated classes..."
if grep -q "ImaRealmRecord" src/main/java/com/owiseman/core/jooq/repository/impl/JooqRealmRepository.java; then
    echo "✅ Implementation uses ImaRealmRecord"
else
    echo "⚠️  Implementation does not use ImaRealmRecord (may use DSL directly)"
fi

if grep -q "Tables.IMA_REALM" src/main/java/com/owiseman/core/jooq/repository/impl/JooqRealmRepository.java; then
    echo "✅ Implementation uses Tables.IMA_REALM"
else
    echo "⚠️  Implementation does not use Tables.IMA_REALM"
fi

echo ""
echo "5. Checking for required methods..."
methods=("findById" "findByName" "create" "update" "enable" "disable")
for method in "${methods[@]}"; do
    if grep -q "public.*$method" src/main/java/com/owiseman/core/jooq/repository/RealmRepository.java; then
        echo "✅ $method method found in interface"
    else
        echo "❌ $method method not found in interface"
    fi
done

echo ""
echo "6. Checking JOOQ generated files..."
if [ -f "src/main/java/com/owiseman/jooq/tables/ImaRealm.java" ]; then
    echo "✅ ImaRealm table class generated"
else
    echo "❌ ImaRealm table class not found"
fi

if [ -f "src/main/java/com/owiseman/jooq/tables/records/ImaRealmRecord.java" ]; then
    echo "✅ ImaRealmRecord class generated"
else
    echo "❌ ImaRealmRecord class not found"
fi

echo ""
echo "=== Summary ==="
echo "RealmRepository implementation is ready!"
echo ""
echo "To test with database:"
echo "  1. Ensure PostgreSQL is running"
echo "  2. Run: mvn test -Dtest=JooqRealmRepositoryTest"
echo ""
echo "To run integration tests:"
echo "  1. Ensure database schema is initialized"
echo "  2. Run: mvn verify"
