import java.net.Socket;
import java.io.*;
import java.lang.*;

public class Client {


    public static class client {

        public static void main(String[] args) {

            try{
                Socket socket=new Socket("10.13.246.153",7788);

                DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
                DataInputStream din = new DataInputStream(socket.getInputStream());


                dout.writeUTF("此处输入录音内容UTF-8");
                dout.flush();

                System.out.println("send first message");

                // start listening thread
                Thread t = new Thread(new Receive(din));
                t.start();

            }

            catch(Exception e){
                e.printStackTrace();}


        }

    }
}

class Receive implements Runnable {
    private DataInputStream din;

    Receive(DataInputStream din) throws IOException {
        this.din = din;

    }

    @Override
    public void run() {
        try {
            String info = null;
            while (true) {
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
