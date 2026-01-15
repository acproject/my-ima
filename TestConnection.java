import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/my-ima";
        String user = "postgres";
        String pass = "ac123456";
        
        System.out.println("Trying: " + url);
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("✅ SUCCESS! Connected to: " + conn.getCatalog());
            
            // Check tables
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, "ima_%", null)) {
                System.out.println("\nFound tables:");
                while (rs.next()) {
                    System.out.println("  - " + rs.getString("TABLE_NAME"));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ FAILED: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
        }
    }
}
