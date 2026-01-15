#!/bin/bash

echo "=== MY-IMA Multi-Step Build ==="
echo ""

# Step 1: Clean and prepare
echo "Step 1: Cleaning target directory..."
mvn clean -q

# Step 2: Compile JOOQ classes first
echo "Step 2: Compiling JOOQ classes..."
mvn compiler:compile \
  -Dmaven.compiler.includeJooq=true \
  -Dmaven.compiler.source=21 \
  -Dmaven.compiler.target=21 \
  -Dmaven.compiler.forceJavacCompilerUse=true \
  -DskipTests=true 2>&1 | grep -E "(Compiling|BUILD|ERROR)" | head -10

# Check if JOOQ classes compiled
if [ ! -f "target/classes/com/owiseman/jooq/Tables.class" ]; then
    echo "❌ JOOQ classes failed to compile"
    exit 1
fi

echo "✅ JOOQ classes compiled successfully"

# Step 3: Compile remaining classes
echo "Step 3: Compiling remaining classes..."
mvn compiler:compile \
  -Dmaven.compiler.includeJooq=true \
  -Dmaven.compiler.source=21 \
  -Dmaven.compiler.target=21 \
  -Dmaven.compiler.forceJavacCompilerUse=true \
  -DskipTests=true 2>&1 | grep -E "(Compiling|BUILD|ERROR)" | head -20

# Check final result
if [ -f "target/classes/com/owiseman/core/jooq/repository/impl/JooqRealmRepository.class" ] && \
   [ -f "target/classes/com/owiseman/core/jooq/repository/impl/JooqUserRepository.class" ]; then
    echo ""
    echo "🎉 SUCCESS: All Repository implementations compiled!"
    echo ""
    echo "Compiled Repository classes:"
    find target/classes -name "*Repository.class" | sort
else
    echo ""
    echo "❌ FAILED: Repository implementations not found"
    exit 1
fi
