package com.wal.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class ClientChat extends JFrame implements ActionListener,KeyListener{

    private JTextArea jta;
    private JScrollPane jsp;
    private JPanel jp;
    private JTextField jtf;
    private JButton jb;
    BufferedWriter bw = null;

    private static int clientPort;
    private static String clientIp;

    static{
        Properties pro = new Properties();
        try {
            pro.load(new FileReader("chat.properties"));
            clientPort = Integer.parseInt(pro.getProperty("clientPort"));
            clientIp = pro.getProperty("clientIp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientChat(){
        jta = new JTextArea();
        jta.setEnabled(false);
        jsp = new JScrollPane(jta);
        jp = new JPanel();
        jtf = new JTextField(10);
        jb = new JButton("发送");

        jp.add(jtf);
        jp.add(jb);

        this.add(jsp,BorderLayout.CENTER);
        this.add(jp,BorderLayout.SOUTH);

        jb.addActionListener(this);
        jtf.addKeyListener(this);

        this.setTitle("客户端");
        this.setSize(300,300);
        this.setLocation(300,300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        try {
            Socket socket = new Socket(clientIp,clientPort);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line = null;
            while((line = br.readLine()) != null){
                jta.append(line + System.lineSeparator());
            }

            //关闭通道
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new ClientChat();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sendTextToServer();
    }

    private void sendTextToServer() {
        String text = jtf.getText();
        text = "客户端对服务端说："+text;
        jta.append(text + System.lineSeparator());

        try {
            bw.write(text);
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
            sendTextToServer();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
