#!/bin/bash

# Fix Tables.java
sed -i '' '/ImaAuditLog/d; /IMA_AUDIT_LOG/d' src/main/java/com/owiseman/jooq/Tables.java

# Fix Public.java  
sed -i '' '/ImaAuditLog/d; /ima_audit_log/d' src/main/java/com/owiseman/jooq/Public.java

# Fix Keys.java
sed -i '' '/ImaAuditLog/d; /IMA_AUDIT_LOG_PKEY/d; /ima_audit_log/d' src/main/java/com/owiseman/jooq/Keys.java

echo "✅ Fixed JOOQ files"
