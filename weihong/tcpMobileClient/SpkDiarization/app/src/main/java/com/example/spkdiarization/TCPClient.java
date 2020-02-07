package com.example.spkdiarization;

import android.content.Intent;
import android.util.Log;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;


class TcpClient implements Runnable{
    private String TAG = "TcpClient";
    private String  serverIP = "10.13.120.182";
    private int serverPort = 7788;
    private PrintWriter pw;
    private InputStream is;
    private DataInputStream dis;
    private boolean isRun = true;
    private Socket socket = null;
    byte buff[]  = new byte[4096];
    private String rcvMsg;
    private int rcvLen;

    public TcpClient(String ip , int port){
        this.serverIP = ip;
        this.serverPort = port;
    }

    public void closeSelf(){
        isRun = false;
    }

    public void send(String msg){
        pw.println(msg);
        pw.flush();
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIP,serverPort);
            socket.setSoTimeout(5000);
            pw = new PrintWriter(socket.getOutputStream(),true);
            is = socket.getInputStream();
            dis = new DataInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (isRun){
            try {
                rcvLen = dis.read(buff);
                rcvMsg = new String(buff,0,rcvLen,"utf-8");
                Log.i(TAG, "Received message:"+ rcvMsg);
                Intent intent =new Intent();
                intent.setAction("tcpClientReceiver");
                intent.putExtra("tcpClientReceiver",rcvMsg);
                MainActivity.currentContext.sendBroadcast(intent); // send the message to main activity
                if (rcvMsg.equals("QuitClient")){   // server request the client to quit
                    isRun = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            pw.close();
            is.close();
            dis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

