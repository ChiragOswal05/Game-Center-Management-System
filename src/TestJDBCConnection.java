import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestJDBCConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/GameCenterDB";
    private static final String USER = "root";
    private static final String PASSWORD = "Chirag05@Chirag05@";

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to MySQL successfully!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("⚠ Connection failed! Error: " + e.getMessage());
        }
    }
}
