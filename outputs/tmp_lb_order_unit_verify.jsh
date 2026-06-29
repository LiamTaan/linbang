var url = "jdbc:mysql://127.0.0.1:3306/linbang?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8";
var user = "root";
var pass = "TanLiMing123!";
var conn = java.sql.DriverManager.getConnection(url, user, pass);
var sql = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'linbang' AND TABLE_NAME = 'lb_order_unit' AND COLUMN_NAME IN ('verify_code','verify_status','verify_time','verify_by','verify_remark') ORDER BY FIELD(COLUMN_NAME,'verify_code','verify_status','verify_time','verify_by','verify_remark')";
var ps = conn.prepareStatement(sql);
var rs = ps.executeQuery();
while (rs.next()) {
    System.out.println(rs.getString(1));
}
rs.close();
ps.close();
conn.close();
/exit
