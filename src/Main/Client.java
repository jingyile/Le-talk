package Main;

import login.Login;
import SQL.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/****
 *聊天程序客户端
 *客户端类Client
 *在initGUI中建立窗口
 *在initNET中建立Socket对象
 ****/
class Client implements ActionListener,KeyListener,Runnable{
    //初始化。。。
    JButton textbtn;
    JFrame f;
    JTextArea outArea;
    JTextField inArea;
    JTextField tf_IP;
    JList<String> list;                 //用户列表
    DefaultListModel<String> listModel;
    JComboBox<String> tf_getOtherUserName;              //选择框
    DefaultComboBoxModel<String> comboBoxModel;
    JButton sendButton;     //发送按钮
    JButton closeButton;     //关闭按钮
    Socket s;     //建立套接字
    String ip;
    String tf_getOUN="所有人";
    String MyName;
    String loginInfo;
    boolean isSend=true;
    boolean isLinked=false;
    boolean isOnline=false;
    DataOutputStream out;
    DataInputStream in;
    //构造方法
    public Client(User user){
        MyName=user.getNickname();
        initGUI();
        initNET();
    }
    //main方法
    public static void main(String[] args){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){}
       // Client client=new Client();
    }
    //构造窗体方法
    public void initGUI(){
        UIManager.put("Button.font", new Font("宋体", Font.PLAIN, 24));
        UIManager.put("Label.font", new Font("楷体", Font.PLAIN, 24));
        UIManager.put("TextField.font", new Font("方正喵呜体", Font.PLAIN, 24));
        UIManager.put("TextArea.font", new Font("fzmwt.ttf", Font.PLAIN, 24));
        Font font = new Font("宋体", Font.PLAIN, 25);
        //outArea.setFont(font);
        //outArea.setForeground(new Color(0,245,255));
//顶部面板
        JPanel topPane=new JPanel();
        JLabel topLabel1=new JLabel("当前用户名:");
        JLabel topLabel0=new JLabel(MyName);
        JLabel topLabel2=new JLabel("服务器IP地址:");
        tf_IP=new JTextField("127.0.0.1",10);
        /*loginButton=new JButton("登陆");
        textbtn=new JButton("测试");
        textbtn.addActionListener(this);
        loginButton.addActionListener(this);*/
        topPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        topPane.add(topLabel1);
        topPane.add(topLabel0);
        topPane.add(topLabel2);
        topPane.add(tf_IP);
        /*topPane.add(loginButton);
        topPane.add(textbtn);*/
//中部面板
        JPanel centerPane=new JPanel();
        outArea=new JTextArea("欢迎使用Le-talk聊天工具客户端！\n",8,10);
        outArea.setEditable(false);
        outArea.setLineWrap(true);
        JScrollPane outAreaScrollPane=new JScrollPane(outArea);
        inArea=new JTextField(10);
        inArea.addKeyListener(this);
        JScrollPane  inAreaScrollPane=new JScrollPane(inArea);
        JSplitPane rightSplitPane=new JSplitPane(JSplitPane.VERTICAL_SPLIT,outAreaScrollPane,inAreaScrollPane);
        rightSplitPane.setOneTouchExpandable(true);
        rightSplitPane.setDividerLocation(350);
        listModel=new DefaultListModel<String>();
        list=new JList<String>(listModel);
        JScrollPane listScrollPane=new JScrollPane(list);
        JSplitPane splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listScrollPane,rightSplitPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);
        outArea.setMinimumSize(new Dimension(500,60));
        listScrollPane.setMinimumSize(new Dimension(60,500));
        centerPane.setLayout(new GridLayout(1,1));
        centerPane.add(splitPane);
//底部面板
        JPanel bottomPane=new JPanel();
        JPanel bPane1=new JPanel();
        JPanel bPane2=new JPanel();
        JLabel label1=new JLabel("对");
        JLabel label2=new JLabel("说");
        comboBoxModel=new DefaultComboBoxModel<String>();
        comboBoxModel.addElement("所有人");
        tf_getOtherUserName=new JComboBox<String>(comboBoxModel);
        tf_getOtherUserName.addActionListener(this);
        closeButton=new JButton("关闭");
        sendButton=new JButton("发送");
        closeButton.addActionListener(this);
        sendButton.addActionListener(this);
        bPane1.setLayout(new FlowLayout(FlowLayout.LEFT));
        bPane1.add(label1);
        bPane1.add(tf_getOtherUserName);
        bPane1.add(label2);
        bPane2.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bPane2.add(closeButton);
        bPane2.add(sendButton);
        bottomPane.setLayout(new FlowLayout());
        bottomPane.add(bPane1);
        bottomPane.add(bPane2);
//构造窗体
        f=new JFrame("MyClient");
        f.setLayout(new BorderLayout());
        f.add(topPane,BorderLayout.NORTH);
        f.add(centerPane,BorderLayout.CENTER);
        f.add(bottomPane,BorderLayout.SOUTH);
        f.setSize(650,550);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
//设置窗体显示在屏幕正中间
        int x=(int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int y=(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        f.setLocation((x-f.getWidth())/2,(y-f.getHeight())/2);
    }
    //网络连接初始化--
    private void initNET(){
        try {
            s = new Socket(tf_IP.getText(),1680);
            outArea.append("与服务器连接成功！\n");
            isLinked=true;
            isOnline=true;
            out=new DataOutputStream(s.getOutputStream());
            in=new DataInputStream(s.getInputStream());
            out.writeUTF("11"+MyName);     // 姓名前加11让服务器识别是登录信息
            out.flush();
            new Thread(this).start();         //启动接收信息线程
        } catch(UnknownHostException ue){
            outArea.append("无法解析的服务器地址！\n聊天室将在5秒后退出！");
            try{Thread.sleep(5000);}catch(InterruptedException ie){ie.printStackTrace();}
            System.exit(0);
        }catch (Exception e) {
            JOptionPane.showMessageDialog(f,"找不到服务器");
        }
    }
    //此方法实现接收并分析处理服务器发送的信息
    public void run(){
        try {
            while(true){
                String msg=in.readUTF();
                String str=msg.substring(0,2);
                String[] arrMsg=msg.split("--");
                if(str.equals("11")){        //11代表登录信息，添加用户名到用户列表
                    listModel.clear();
                    comboBoxModel.removeAllElements();
                    comboBoxModel.addElement("所有人");
                    for(int i=1;i<arrMsg.length;i++){
                        listModel.addElement(arrMsg[i]);
                        if(arrMsg[i].equals(MyName)) ;
                        else  comboBoxModel.addElement(arrMsg[i]);
                    }
                }
                else if(str.equals("22")){              //22代表聊天信息，显示在窗体上
                    outArea.append(msg.substring(2)+"\n");
                }
                else if(str.equals("44")){              //44代表系统通知，显示在窗体上
                    outArea.append("系统通知："+msg.substring(2)+"\n");
                }
                else  outArea.append("其他信息"+msg+"\n");       //显示其他信息
            }
        }catch (IOException ioe) {
            isOnline=false;
            //ioe.printStackTrace();
            System.out.println("Sockt Closed");
        }catch(Exception e){
            isOnline=false;
            JOptionPane.showMessageDialog(f,"找不到服务器!");
            e.printStackTrace();
        }
    }
    //定义监听器
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==closeButton) {//关闭按钮
            try{
                s.close();
            }catch(Exception ee){}
            System.exit(0);
        }
        else if(e.getSource()==sendButton&&tf_getOUN.equals("所有人")){//发送按钮
            if(!isOnline)  JOptionPane.showMessageDialog(f,"请先登录！");
            else {
                if(inArea.getText().equals(""))  JOptionPane.showMessageDialog(f,"输入值不能为空！");
                else try{
                    out.writeUTF("22--"+MyName+"--"+inArea.getText());
                    out.flush();
                }catch(Exception ee){
                    outArea.append("找不到服务器！\n");
                }finally{
                    inArea.setText(null);
                }
            }//end else
        }
        else if(e.getSource()==sendButton&&!(tf_getOUN.equals("所有人"))){
            if(inArea.getText().equals(""))  JOptionPane.showMessageDialog(f,"输入不能为空！");
            try{
                out.writeUTF("33--"+MyName+"--"+tf_getOUN+"--"+inArea.getText());
                out.flush();
            }catch(Exception ee){
                outArea.append("用户失去连接！\n");
            }finally{
                inArea.setText(null);
            }
        }
        else if(e.getSource()==tf_getOtherUserName){//选择框
            tf_getOUN=(String)tf_getOtherUserName.getSelectedItem();

        }

    }

    public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
            if(!isOnline)  JOptionPane.showMessageDialog(f,"请先登录！") ;
            else if(inArea.getText().equals(""))  JOptionPane.showMessageDialog(f,"输入信息不能为空！");
            else {
                if(tf_getOUN.equals("所有人")){
                    try{
                        out.writeUTF("22--"+MyName+"--"+inArea.getText());
                        out.flush();
                    }catch(Exception ee){
                        outArea.append("找不到服务器！\n");
                    }finally{
                        inArea.setText(null);
                    }//end finally
                }//end if
                else {
                    try{
                        out.writeUTF("33--"+MyName+"--"+tf_getOUN+"--"+inArea.getText());
                        out.flush();
                    }catch(Exception ee){
                        outArea.append("失去连接！\n");
                    }finally{
                        inArea.setText(null);
                    }//end finally
                }//end else
            }//end else
        }//end if
    }
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}
}