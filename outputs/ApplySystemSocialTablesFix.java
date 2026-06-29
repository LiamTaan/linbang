import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ApplySystemSocialTablesFix {

    public static void main(String[] args) throws Exception {
        String sql = Files.readString(Path.of("D:/user_wuyou/local_life_helper/outputs/fix_system_social_tables.sql"));
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/linbang?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8",
                "root",
                "TanLiMing123!");
             Statement stmt = conn.createStatement()) {
            for (String chunk : sql.split(";\\s*(?:\\r?\\n|$)")) {
                String trimmed = chunk.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                stmt.execute(trimmed);
            }
            System.out.println("OK");
        }
    }
}
