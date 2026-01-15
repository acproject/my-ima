import java.sql.*;

public class test_db {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String pass = "ac23456";
        
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("✅ Connected to PostgreSQL!");
            System.out.println("Database: " + conn.getCatalog());
            
            // Check if my_ima database exists
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getCatalogs()) {
                while (rs.next()) {
                    String dbName = rs.getString("TABLE_CAT");
                    if (dbName.equals("my_ima") || dbName.equals("postgres")) {
                        System.out.println("Found database: " + dbName);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Connection failed!");
            System.out.println("Error: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
        }
    }
}
