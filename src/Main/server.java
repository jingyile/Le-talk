package Main;

import java.io.*;
import java.net.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
/****
 *聊天程序服务器端
 *服务器类Server
 *在initGUI中建立窗口
 *在initNET中建立ServerSocket对象并调用方法accept接受登陆用户
 ****/
class Server implements ActionListener{
    private JTextArea outArea;   //文本框，用于显示信息
    private ServerSocket server;
    private BManager bMan=new BManager();  //消息广播
    Map<Socket,String> clientList=new HashMap<Socket,String>();

    //Server构造方法
    public Server(){
        initGUI();
    }
    //Server类的main方法
    public static void main(String[] args){
//设置swingGUI的L&F为当前运行平台的L&F
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){}
        Server server=new Server();
        server.startServer();
    }
    //initGUI方法编辑窗口
    public void initGUI(){
//输出窗口outArea
        outArea=new JTextArea("欢迎使用Le-talk聊天工具服务器！\n",2,10);
        outArea.setEditable(false);
        outArea.setLineWrap(true);
        JScrollPane outAreaScrollPane=new JScrollPane(outArea);
        //关闭按钮
        JPanel bottomPane=new JPanel();
        JButton closeButton=new JButton("关闭");
        closeButton.addActionListener(this);
        bottomPane.setLayout(new FlowLayout());
        bottomPane.add(closeButton);
//设置窗体
        JFrame f=new JFrame("MyServer");
        f.setLayout(new BorderLayout());
        f.add(outAreaScrollPane,BorderLayout.CENTER);
        f.add(bottomPane,BorderLayout.SOUTH);
        f.setSize(500,500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    //启动服务器
    public void startServer(){
        try{
            server=new ServerSocket(1680);   //创建服务器套接字
            outArea.append("套接字建立完毕,等待客户端连接！\n");
            while(true){
                Socket socket=server.accept();
                String strIP = socket.getInetAddress().toString();  //登陆者的ip
                Chat_Thread ct=new Chat_Thread(socket);
                ct.start();   //启动线程
                bMan.add(socket);   //添加套接字
                bMan.sendClientInfo();//使用套接字输出当前聊天人数
                //bMan.sendToAll(strIP+"/加入聊天室");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    //定义监听器
    public void actionPerformed(ActionEvent e){
        System.exit(0);
    }
//与客户机进行通讯的线程类
    /***
     *接收信息，并对其进行处理
     *11代表登录信息，信息格式：11name
     *22代表公聊信息，信息格式：22--name--msg
     *33代表私聊信息，信息格式：33--towho--name--msg
     *发送信息，并进行处理
     *11代表在线列表，信息格式：11--name1--name2--name3--...
     *22代表发送信息，信息格式：22msg
     *44代表通知信息，信息格式：44msg
     ***/
    class Chat_Thread extends Thread{
        Socket socket;
        private DataInputStream reader;
        private DataOutputStream writer;
        Chat_Thread(Socket socket){
            this.socket=socket;
        }
        public void run(){
            try{
                reader=new DataInputStream(socket.getInputStream());
                writer=new DataOutputStream(socket.getOutputStream());
                String msg;
                //msg获取消息
                while((msg=reader.readUTF())!=null){
                    String str=msg.substring(0, 2);   //截取前两个个字符
                    int a=Integer.parseInt(str);   //强制转换成int
                    String[] arrMsg=msg.split("--");  //将获取的消息以"--"符号为标志分解成数组
                    switch(a){
                        case 11 :   //当消息以11开头的时候，将登陆者的信息储存到hashmap之中，并向客户端发送新的在线列表
                            String strName=msg.substring(2);   //获取登陆者名字，消息格式“11eko”
                            outArea.append(strName+"登陆了"+"\n");
                            bMan.sendToAll("22"+strName+"成功登录到聊天室！");  //广播谁登陆了
                            clientList.put(this.socket,strName);  //加入到HashMap中
                            funList(clientList);   //广播在线列表
                            break;
                        case 22:   //当消息以22开头的时候，内容为“22--eko--内容”
                            //构造消息，arrMsg[0]=消息头，arrMsg[1]消息发送者，arrMsg[2]消息内容
                            msg=arrMsg[0]+getTime()+" \t  "+arrMsg[1]+"说：\n    "+arrMsg[2];
                            bMan.sendToAll(msg);//向所有人广播消息
                            break;
                        case 33://消息以33开头时候，内容为“33--sandal--eko--内容”
                            //arrMsg[1]为说话对象，arrMsg[２]为说话人，arrMsg[３]为消息内容
                            if(arrMsg[1].equals("所有人")){   //当说话对象为＂所有人＂的时候
                                msg="22"+arrMsg[2]+"说：\t"+getTime()+"\n    "+arrMsg[3];  //构造消息＂22eko说：内容＂
                                bMan.sendToAll(msg);   //向所有人发送消息
                            }//end if
                            else{               //其他情况就是向具体的某个人发送消息了
                                Socket socketOne;
                                Set set = clientList.keySet();  //使用keySet方法获取所有key值
                                Iterator it = set.iterator();  //使用Iterator（迭代器）来遍历数据
                                while (it.hasNext()) {      //返回是否还有没被访问过的对象
                                    Object ok=it.next();  //返回下一个没被访问过的对象
                                    Object ov=clientList.get(ok);  //get方法返回拥有key的元素
                                    if(ov.equals(arrMsg[2])){  //如果在client中找到"消息发给谁"的时候，发给对方
                                        socketOne=(Socket)ok;//强制转换成key值类型；
                                        bMan.sendToONE(socketOne,"22（悄悄话）"+arrMsg[1]+"对你说：\t"+getTime()+"\n    "+arrMsg[3]);
                                    }else if(ov.equals(arrMsg[1])){   //如果在client中找到"发消息的人"的时候，发给他自己
                                        socketOne=(Socket)ok;
                                        bMan.sendToONE(socketOne,"22（悄悄话）你对"+arrMsg[2]+"说：\t"+getTime()+"\n    "+arrMsg[3]);
                                    }//end else if
                                }//end while
                            }//end else
                            break;
                    }//end switch
                }//end while
                //bMan.sendToAll(msg);
            }catch(Exception e){}finally{
                try {
                    bMan.remove(socket);
                    if(reader !=null) reader.close();
                    if(writer !=null) writer.close();
                    if(socket !=null) socket.close();
                    if(clientList.containsKey(socket)){
                        bMan.sendToAll("22"+clientList.get(socket)+"离开了...");  //广播消息，谁离开了
                        outArea.append(clientList.get(socket)+"退出了聊天室！\n");
                        clientList.remove(socket);    //删除socket
                        funList(clientList);   //广播在线列表
                    }//end if
                    reader=null;
                    writer=null;
                    socket=null;
                    bMan.sendClientInfo();//广播在线人数
                } catch (Exception e) {}
            }//end finally
        }//end run()

        void funList(Map clientList){         // 广播在线列表
            String strList="";                       //在线列表
            Set set = clientList.keySet();    //使用keySet方法获取所有key值
            Iterator it = set.iterator();     //使用Iterator（迭代器）来遍历数据
            while(it.hasNext()){             //把用户名称发给在线所有客户端
                //构造在线列表格式strList=11--one--two--three
                strList+="--";
                strList+=clientList.get(it.next());
            }
            bMan.sendToAll("11"+strList);
        }
        public String getTime(){
            Calendar ca=Calendar.getInstance();
            ca.setTime(new Date());
            Integer[] T=new Integer[]{ca.get(Calendar.HOUR),ca.get(Calendar.MINUTE),ca.get(Calendar.SECOND)};
            String[] TT={null,null,null};
            for(int i=0;i<3;i++){
                if(T[i]<10)  TT[i]="0"+T[i].toString();
                else TT[i]=T[i].toString();
            }
            return TT[0]+":"+TT[1]+":"+TT[2];
        }
    }//end class

    /****
     *广播消息类
     ****/

    class BManager extends Vector{
        BManager(){}
        void add(Socket sock){
            super.add(sock);
        }
        void remove(Socket sock){
            super.remove(sock);
        }
        synchronized void sendToAll(String msg){   //给所有人广播函数
            DataOutputStream writer=null;
            Socket sock;
            for(int i=0;i<size();i++){   //执行循环
                sock=(Socket)elementAt(i);//获取第i个套接字
                try{       //获取第i个套接字输出流
                    writer=new DataOutputStream(sock.getOutputStream());
                }catch(Exception ie){}
//使用第i各套接字输出流，输出消息
                try{if(writer!=null)  writer.writeUTF(msg);}catch(IOException ioe){}
            }//end for
        }
        synchronized void sendToONE(Socket socket,String msg){   //私聊函数
            DataOutputStream writer=null;
            Socket sock;
            for(int i=0;i<size();i++){
                sock=(Socket)elementAt(i);
                if(socket==sock){   //与给所有人广播函数类似，仅加入了判断，只有当socket管理器中的socket等于传入的socket的时候才发送消息
                    try{
                        writer=new DataOutputStream(sock.getOutputStream());
                    }catch(Exception ie){}
                    try{if(writer!=null)  writer.writeUTF(msg);}catch(IOException ioe){}
                }
            }//end for
        }
        synchronized void sendClientInfo(){
            String info="44当前聊天人数："+size();
            //System.out.println(info);
            sendToAll(info);
        }
    }
}

