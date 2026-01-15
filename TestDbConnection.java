import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDbConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/my-ima";
        String username = "postgres";
        String password = "ac23456";

        System.out.println("Attempting to connect to database...");
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connection successful!");
            System.out.println("Database: " + conn.getCatalog());
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Connection failed!");
            System.out.println("Error code: " + e.getErrorCode());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
