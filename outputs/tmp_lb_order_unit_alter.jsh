var url = "jdbc:mysql://127.0.0.1:3306/linbang?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8";
var user = "root";
var pass = "TanLiMing123!";
var conn = java.sql.DriverManager.getConnection(url, user, pass);
var stmt = conn.createStatement();
try {
    stmt.executeUpdate("ALTER TABLE lb_order_unit ADD COLUMN verify_code varchar(32) NULL DEFAULT NULL AFTER appeal_expire_time, ADD COLUMN verify_status varchar(32) NULL DEFAULT NULL AFTER verify_code, ADD COLUMN verify_time datetime NULL DEFAULT NULL AFTER verify_status, ADD COLUMN verify_by bigint NULL DEFAULT NULL AFTER verify_time, ADD COLUMN verify_remark varchar(255) NULL DEFAULT NULL AFTER verify_by");
    System.out.println("ALTER_OK");
} catch (java.sql.SQLException e) {
    System.out.println("ALTER_ERR:" + e.getErrorCode() + ":" + e.getMessage());
}
stmt.close();
conn.close();
/exit
