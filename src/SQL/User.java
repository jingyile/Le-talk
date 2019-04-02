package SQL;

public class User {
    private String username;
    private String password;
    private String nickname;
    private String num;

    public User(String username, String password, String nickname, String num) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.num = num;
    }
    public User(String username,String password)
    {
        this.username=username;
        this.password=password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
