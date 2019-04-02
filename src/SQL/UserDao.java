package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public User login(Connection con, User user) throws SQLException {
        User resultUser = null;
        String sql = "select * from users where username=? and password=?";
        PreparedStatement ps = con.prepareStatement(sql);//
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            resultUser = user;
            String nickname=rs.getString(3);
            resultUser.setNickname(nickname);
        }
        return resultUser;
    }

    public boolean register(Connection con, User user) throws SQLException {
        String name = user.getUsername();
        String password = user.getPassword();
        String ncname = user.getNickname();
        String num = user.getNum();
        String sql1 = "select * from users where username=?";
        PreparedStatement ps = con.prepareStatement(sql1);//
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if(rs.next())
        {
            return false;
        }
        else {
            String sql2 = "insert into users values('" + name + "','" + password + "','" + ncname + "','" + num + "')";
           ps=con.prepareStatement(sql2);
            ps.executeUpdate();
            ps.close();
            return true;
        }
    }
}
