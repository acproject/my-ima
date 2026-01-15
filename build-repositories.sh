#!/bin/bash

echo "=== MY-IMA Build Script ==="
echo "Step 1: Compiling JOOQ classes..."
javac -cp "$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" -d target/classes \
  src/main/java/com/owiseman/jooq/*.java \
  src/main/java/com/owiseman/jooq/tables/*.java \
  src/main/java/com/owiseman/jooq/tables/records/*.java 2>&1 | grep -v "注:" || true

echo "Step 2: Compiling core domain and exception classes..."
javac -cp "target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" -d target/classes \
  src/main/java/com/owiseman/core/domain/*.java \
  src/main/java/com/owiseman/core/exception/*.java 2>&1 | grep -v "注:" || true

echo "Step 3: Compiling repository interfaces..."
javac -cp "target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" -d target/classes \
  src/main/java/com/owiseman/core/jooq/repository/*.java 2>&1 | grep -v "注:" || true

echo "Step 4: Compiling repository implementations..."
javac -cp "target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" -d target/classes \
  src/main/java/com/owiseman/core/jooq/repository/impl/*.java 2>&1 | grep -v "注:" || true

echo "Step 5: Compiling remaining source files..."
javac -cp "target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" -d target/classes \
  src/main/java/com/owiseman/App.java 2>&1 | grep -v "注:" || true

echo ""
echo "=== Compilation Complete ==="
echo "Checking compiled classes..."
if [ -f "target/classes/com/owiseman/core/jooq/repository/impl/JooqRealmRepository.class" ] && \
   [ -f "target/classes/com/owiseman/core/jooq/repository/impl/JooqUserRepository.class" ]; then
    echo "✅ SUCCESS: All Repository implementations compiled successfully!"
    echo ""
    echo "Compiled classes:"
    find target/classes -name "*Repository.class" | sort
else
    echo "❌ FAILED: Repository implementations not found"
    exit 1
fi
