import java.io.*;
import java.net.Socket;

public class ClientSendFile {


    public static class client {

        public static void main(String[] args) {

            try{
                Socket socket=new Socket("10.13.246.153",7788);

                DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
                DataInputStream din = new DataInputStream(socket.getInputStream());


                System.out.println("send first message");

                // start listening thread
                Thread tr = new Thread(new ReceiveFile(din));
                tr.start();

                //start sending thread
                Thread ts = new Thread(new SendFile(dout));
                ts.start();

            }

            catch(Exception e){
                e.printStackTrace();}


        }

    }
}

class SendFile implements Runnable{
    private DataOutputStream dout;

    SendFile(DataOutputStream dout){
        this.dout = dout;
    }
    int length = 0;
    double sumL = 0 ;
    byte[] sendBytes = null;
    FileInputStream fis = null;
    boolean bool = false;

    @Override
    public void run() {
        File file = new File("C:\\Users\\lwhli\\Desktop\\summer project\\VoiceMeeting\\VoiceMeeting\\weihong\\tcpClient\\src\\1.wav");
        if(file.exists()){
            try {
                fis = new FileInputStream(file);

                dout.writeUTF("start");
                // send file name
                dout.writeUTF(file.getName());
                System.out.println(file.getName());
                dout.flush();

                // send file length
                dout.writeLong(file.length());
                System.out.println(file.length());
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
    private DataInputStream din;

    ReceiveFile(DataInputStream din) throws IOException {
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
