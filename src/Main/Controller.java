package Main;

import SQL.DB;
import SQL.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import login.Login;
import SQL.User;
import register.Register;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

import static java.lang.Thread.sleep;


public class Controller {
    private int flag = 0;
    @FXML
    private Text actiontarget;
    @FXML
    private TextField username0;
    @FXML
    private TextField password0;
    @FXML
    private TextField password00;
    @FXML
    private TextField nickname0;
    @FXML
    private TextField num0;
    UserDao userDao = new UserDao();
    DB db = new DB();
    Register register = new Register();

    @FXML
    protected void ButtonAction1(ActionEvent actionEvent) throws Exception {//登录
        {
            UIManager.put("JOptionPane.font", new Font("宋体", Font.PLAIN, 24));
            String username = username0.getText();
            String password = password0.getText();
            /*
            actiontarget.setText(password);
            if (username.equals("jingyile")&& password.equals("123456")) {
                if (flag == 0) {
                    actiontarget.setText("正在登录.....");
                    Client clint = new Client();
                    flag = 1;
                } else {
                    JOptionPane.showMessageDialog(null, "您已经登陆了，不可重复登录！", "这是一个提示框框", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "账号或密码错误！", "这是一个提示框框", JOptionPane.ERROR_MESSAGE);
            }*/
            Connection con = null;
            try {
                User user = new User(username, password);
                con = db.getCon();
                User currentUser = userDao.login(con, user);
                if (currentUser == null) {
                    JOptionPane.showMessageDialog(null, "账号或密码错误！", "这是一个提示框框", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (flag == 0) {
                        actiontarget.setText("正在登录.....");
                        Login login =new Login();
                        login.closeWindow();
                        Client clint = new Client(currentUser);
                        flag = 1;
                    } else {
                        JOptionPane.showMessageDialog(null, "您已经登陆了，不可重复登录！", "这是一个提示框框", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
        public void ButtonAction2 (ActionEvent event) throws Exception {

            UIManager.put("Button.font", new Font("宋体", Font.PLAIN, 20));
            UIManager.put("Label.font", new Font("楷体", Font.PLAIN, 24));
            UIManager.put("TextField.font", new Font("方正喵呜体", Font.PLAIN, 20));
            UIManager.put("TextArea.font", new Font("fzmwt.ttf", Font.PLAIN, 20));
            UIManager.put("JOptionPane.font", new Font("宋体", Font.PLAIN, 24));
            String username = username0.getText();
            String password = password0.getText();
            String passwordd = password00.getText();
            String nickname= nickname0.getText();
            String num = num0.getText();
            System.out.println(username);
            System.out.println(password);
            System.out.println(nickname);
            System.out.println(num);
            Connection con = null;
           try {
                User user = new User(username, password, nickname, num);
                con = db.getCon();
                boolean flag=userDao.register(con,user);
                if(!flag)
                {
                    JOptionPane.showMessageDialog(null, "抱歉，该用户名已注册！", "这是一个提示框框", JOptionPane.ERROR_MESSAGE);
                }
                else{
           JOptionPane.showMessageDialog(null, "恭喜您，注册成功，可直接关闭此页面进行登录！", "这是一个提示框框", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void changeWindow (ActionEvent event) throws Exception {
        try {
            register.showWindow();
            //sleep(10000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

            System.out.println("lallal");
            //关于这里，有个很疑惑的地方。为什么register.showWindow不是立刻执行，而是同样延迟了。
           // register.closeWindow();
        }

    }
