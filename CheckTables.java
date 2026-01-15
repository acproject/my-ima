import java.sql.*;

public class CheckTables {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/my-ima";
        Connection conn = DriverManager.getConnection(url, "postgres", "ac123456");
        DatabaseMetaData meta = conn.getMetaData();
        
        try (ResultSet rs = meta.getTables(null, null, "ima_audit_log", null)) {
            if (rs.next()) {
                System.out.println("✅ Table exists: " + rs.getString("TABLE_NAME"));
                System.out.println("Type: " + rs.getString("TABLE_TYPE"));
                
                // Get columns
                try (ResultSet cols = meta.getColumns(null, null, "ima_audit_log", null)) {
                    System.out.println("\nColumns:");
                    while (cols.next()) {
                        System.out.println("  - " + cols.getString("COLUMN_NAME") + 
                            " (" + cols.getString("TYPE_NAME") + ")");
                    }
                }
            } else {
                System.out.println("❌ Table ima_audit_log not found");
            }
        }
        
        conn.close();
    }
}
