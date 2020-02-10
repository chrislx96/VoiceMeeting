package com.app.androidkt.VoiceMeeting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

public class TcpUploadClient extends Socket {
    private static String SERVER_IP;
    private static int SERVER_PORT;
    private Socket client;
    private static DataOutputStream dout;
    private static DataInputStream din ;

    public TcpUploadClient(String ip, int port) {
        SERVER_IP = ip;
        SERVER_PORT = port;
        this.client = this;
    }

    public void sendFile(String path) {
            try{
                // start listening thread
                Thread tr = new Thread(new ReceiveFile());
                tr.start();
                //start sending thread
                Thread ts = new Thread(new SendFile(path));
                ts.start();
            }
            catch(Exception e){
                e.printStackTrace();}
    }

    class SendFile implements Runnable {
        private String path;
        SendFile(String path){
            this.path = path;
        }
        FileInputStream fis = null;
        @Override
        public void run() {
            File file = new File(path);
            if(file.exists()){
                try {
                    client=new Socket(SERVER_IP,SERVER_PORT);

                    dout= new DataOutputStream(client.getOutputStream());
                    din = new DataInputStream(client.getInputStream());
                    System.out.println("send first message");

                    fis = new FileInputStream(file);
//                    // send file name
                    dout.writeUTF("start");
                    dout.flush();

                    dout.write(file.getName().getBytes());
                    dout.flush();

                    // send file length
                    dout.writeLong(file.length());
                    dout.flush();
                    System.out.println(file.length());

                    System.out.println("start sending");
                    byte bytes[] = new byte[1024];
                    int length = 0;
                    long progress = 0;

                    while((length = fis.read(bytes, 0, bytes.length)) != -1){
                        dout.write(bytes, 0, length);
                        dout.flush();
                        progress += length;
                        System.out.print("| " + (100*progress/file.length()) + "% |");
                    }


                    System.out.println("finish sending");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ReceiveFile implements Runnable {

        ReceiveFile() {
        }

        @Override
        public void run() {
            try {
                String info;
                while (din!=null) {
                    info = din.readUTF();
                    if ("null".equals(info)) {
                        break;
                    }
                    System.out.println("doing visualization stuff here：" + info);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}






