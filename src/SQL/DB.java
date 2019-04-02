package SQL;

import java.sql.*;

public class DB {
    private String url = "jdbc:mysql://localhost:3306/jingyile?characterEncoding=utf8";
    private String user = "root";
    private String password = "Answer1025.";
    private String driver = "com.mysql.jdbc.Driver";
    public Connection getCon() throws Exception {
        Class.forName(driver);
        Connection con = DriverManager.getConnection(url, user, password);
        return con;
    }

    public static void getClose(Connection con) throws SQLException {
        if (con != null) {
            con.close();
        }
    }

    public static void main(String[] args) {
        DB db = new DB();
        try {
            db.getCon();
            System.out.println("测试连接数据库，连接成功");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("测试连接数据库，连接失败");
        }

    }

}
