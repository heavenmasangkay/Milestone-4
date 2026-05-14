package boutiquecapsulehotelsystem;
import java.sql.*;

public class DBConnection {
    public static Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/boutique_hotel";
        String user = "root";
        String pass = "WJ28@krhps";
        return DriverManager.getConnection(url, user, pass);
    }
}