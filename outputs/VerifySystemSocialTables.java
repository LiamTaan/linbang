import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class VerifySystemSocialTables {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/linbang?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8",
                "root",
                "TanLiMing123!");
             Statement stmt = conn.createStatement()) {
            print(stmt, "SHOW TABLES LIKE 'system_social_user'");
            print(stmt, "SHOW TABLES LIKE 'system_social_user_bind'");
        }
    }

    private static void print(Statement stmt, String sql) throws Exception {
        try (ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        }
    }
}
