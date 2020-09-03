package com.wal.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class ServerChat extends JFrame implements ActionListener,KeyListener{
    private JTextArea jta;
    private JScrollPane jsp;
    private JPanel jp;
    private JTextField jtf;
    private JButton jb;
    BufferedWriter bw = null;

    private static int serverPort;

    static{
        Properties pro = new Properties();
        try {
            pro.load(new FileReader("chat.properties"));
            serverPort = Integer.parseInt(pro.getProperty("serverPort"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ServerChat(){
        jta = new JTextArea();
        jta.setEnabled(false);
        jsp = new JScrollPane(jta);
        jp = new JPanel();
        jtf = new JTextField(10);
        jb = new JButton("发送");

        jp.add(jtf);
        jp.add(jb);

        this.add(jsp, BorderLayout.CENTER);
        this.add(jp,BorderLayout.SOUTH);

        jb.addActionListener(this);
        jtf.addKeyListener(this);

        this.setTitle("服务端");
        this.setSize(300,300);
        this.setLocation(600,300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);

            //等待客户端的连接
            Socket socket = serverSocket.accept();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line = null;
            while((line = br.readLine())!= null){
                //每读取一行后，加到jta里进行换行
                jta.append(line + System.lineSeparator());
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerChat();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sendTextToClient();
    }

    private void sendTextToClient() {
        String text = jtf.getText();
        text = "服务端对客户端说："+ text;
        jta.append(text+System.lineSeparator());

        try {
            bw.write(text);
            //传换行过去,读的时候便判断，一行一行读。
            bw.newLine();
            bw.flush();
            jtf.setText("");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            sendTextToClient();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
